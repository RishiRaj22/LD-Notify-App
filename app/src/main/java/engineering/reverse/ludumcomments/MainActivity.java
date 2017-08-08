package engineering.reverse.ludumcomments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.game_name_set)
    EditText gameNameSet;
    @BindView(R.id.enter_data_layout)
    LinearLayout enterDataLayout;
    @BindView(R.id.game_name)
    TextView gameName;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private MyRecyclerAdapter recyclerAdapter;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;
    private ArrayList<CommentData> commentDatas;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (queue == null)
            queue = Volley.newRequestQueue(getApplicationContext());

        prefs = getSharedPreferences("data", MODE_PRIVATE);
        int gameno = prefs.getInt("gameno", -1);
        if (gameno == -1)
            return;

        load();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void fetchNodeNames() {
        String newNodeUrl = "https://quarkbackend.com/getfile/rishiraj22/state";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(newNodeUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String node = "node", note = "note", vx = "vx";
                try {
                    node = response.getString("node");
                    note = response.getString("note");
                    vx = response.getString("vx");
                } catch (JSONException e) {
                    Log.e("QUARK_BACKEND", "Quark backend not working");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("QUARK_BACKEND", "Quark backend not working");
            }
        });

    }

    private void load() {
        enterDataLayout.setVisibility(View.GONE);
        commentDatas = new ArrayList<CommentData>();

        final String vx = prefs.getString("vx", null);
        final String note = prefs.getString("note", null);
        final String node = prefs.getString("node", null);
        int gameno = prefs.getInt("gameno", -1);
        if (vx == null || note == null || node == null || gameno == -1) {
            Log.d("PREFS", "Values not loaded from prefs. Fatal error");
            initGameLoader(prefs.getString("link", null));
            return;
        }
        Log.d("PREFS_main", node + " " + note + " " + vx + " Game #" + gameno);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        String noteUrl = "https://api.ldjam.com/" + vx + "/" + note + "/get/" + gameno;
        String nodeUrl = "https://api.ldjam.com/" + vx + "/" + node + "/get/" + gameno;

        Log.d("LOAD", noteUrl);
        Log.d("LOAD", nodeUrl);

        JsonObjectRequest noteRequest =
                new JsonObjectRequest(
                        noteUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray(note);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    int comments = prefs.getInt("comments", -1);
                                    ArrayList<Integer> authors = new ArrayList<>();

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int author = jsonObject.getInt("author");
                                        if(!authors.contains(author)) {
                                            Log.d("REQUEST_AUTHOR","New author " + author + " added");
                                            authors.add(author);
                                        }

                                        if (i > comments)
                                            commentDatas.add(
                                                    new CommentData(
                                                            author,
                                                            jsonObject.getString("body"),
                                                            String.valueOf(i + 1).concat(" !"),
                                                            jsonObject.getString("modified")));
                                        else
                                            commentDatas.add(
                                                    new CommentData(
                                                            author,
                                                            jsonObject.getString("body"),
                                                            String.valueOf(i + 1),
                                                            jsonObject.getString("modified")));
                                    }
                                    if (comments != 0) {
                                        Log.d("REQUEST", "Author request to be executed");
                                        int len = authors.size();
                                        for (int i = 0; i < len; i += 30) {
                                            StringBuilder authorSearch = new StringBuilder("https://api.ldjam.com/" + vx + "/" + node + "/get/" + authors.get(i));
                                            for (int j = 1; j < 30 && (i + j) < len; j++) {
                                                authorSearch.append('+');
                                                authorSearch.append(authors.get(i + j));
                                            }
                                            String requestUrl = authorSearch.toString();
                                            Log.d("REQUEST_AUTHOR", requestUrl);
                                            JsonObjectRequest authorsRequest = new JsonObjectRequest(
                                                    requestUrl,
                                                    null,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try {
                                                                JSONArray jsonArray = response.getJSONArray(node);
                                                                long time = System.currentTimeMillis();
                                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                                    int id = jsonObject.getInt("id");
                                                                    String name = jsonObject.getString("name");
                                                                    for (CommentData data : commentDatas) {
                                                                        if (data.getId() == id) {
                                                                            data.setName(name);
                                                                            Log.d("REQUEST_AUTHOR",name);
                                                                        }
                                                                    }
                                                                    //Highly inefficient n^2 algo
                                                                }
                                                                long timeEnd = System.currentTimeMillis();
                                                                Log.d("COMMENTER_NAME", "Processing took " + (timeEnd - time) + "ms");
                                                                recyclerAdapter.notifyDataSetChanged();
                                                                recyclerView.refreshDrawableState();
                                                            } catch (JSONException ex) {
                                                                ex.printStackTrace();
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                }
                                            });
                                            queue.add(authorsRequest);
                                        }
                                    }
                                    recyclerAdapter = new MyRecyclerAdapter(commentDatas);
                                    recyclerView.setAdapter(recyclerAdapter);
                                    editor.putInt("comments", commentDatas.size());
                                    editor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT);
                    }
                });
        JsonObjectRequest nodeRequest =
                new JsonObjectRequest(
                        nodeUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("node");
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    gameName.setText(jsonObject.getString("name"));
                                    jsonObject = jsonObject.getJSONObject("magic");
                                    double grade = jsonObject.getDouble("grade");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    if (grade > prefs.getFloat("addedGradings", 0)) {
                                        editor.putFloat("addedGradings", (float) grade);
                                        editor.commit();
                                        rating.setText(
                                                "Rating: " + String.valueOf(grade).concat(" !"));
                                    } else
                                        rating.setText(
                                                "Rating: " + String.valueOf(grade));
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT);
                    }
                });

        noteRequest.setRetryPolicy(VolleyUtils.getRetryPolicy());
        nodeRequest.setRetryPolicy(VolleyUtils.getRetryPolicy());

        queue.add(noteRequest);
        queue.add(nodeRequest);
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        String link = gameNameSet.getText().toString();
        initGameLoader(link);
    }

    private void initGameLoader(final String link) {
        if (link == null) return;
        int pos = -1, len = link.length();
        for (int i = 0; i < len - 11; i++) {
            if (link.substring(i, i + 9).equalsIgnoreCase("ldjam.com")) {
                pos = i + 9;
                break;
            }
        }
        if (pos == -1) {
            Log.d("LD_LINK", "LUDUM DARE ENTRY LINK NOT VALID");
            //// TODO: 07-08-2017 Link not valid notify user
            return;
        }
        final String relativeLink = link.substring(pos);
        String newNodeUrl = "https://quarkbackend.com/getfile/rishiraj22/state";
        JsonObjectRequest nodeRequest = new JsonObjectRequest(newNodeUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String node = "node", note = "note", vx = "vx";
                String root;
                try {
                    node = response.getString("node");
                    note = response.getString("note");
                    vx = response.getString("vx");
                } catch (JSONException e) {
                    Log.e("QUARK_BACKEND", "Quark backend not working");
                    node = "node";
                    note = "note";
                    vx = "vx";
                }
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString("node", node);
                editor.putString("note", note);
                editor.putString("vx", vx);
                root = "https://api.ldjam.com/" + vx + "/node/walk/1";

                int len = relativeLink.length();
                String nodeLink = null;

                Log.d("VOLLEY_REQ", relativeLink + " Length: " + len);
                nodeLink = root + relativeLink;

                Log.d("VOLLEY_REQ", "Link " + nodeLink);
                JsonObjectRequest gameNoRequest =
                        new JsonObjectRequest(
                                nodeLink,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int status = response.getInt("status");
                                            if (status != 200) {
                                                Log.d("VOLLEY_REQ", "Status is not 200");
                                                return;
                                            }
                                            Log.d("VOLLEY_REQ", response.toString());
                                            Log.d("VOLLEY_REQ", "Status: 200");

                                            int gameno = response.getInt("node");
                                            editor.putInt("gameno", gameno);
                                            editor.putString("link", link);
                                            editor.apply();
                                            AlarmManager alarmManager = (AlarmManager)
                                                    getApplicationContext().
                                                            getSystemService(ALARM_SERVICE);
                                            PendingIntent pendingIntent = PendingIntent
                                                    .getBroadcast(
                                                            MainActivity.this,
                                                            112,
                                                            new Intent(
                                                                    MainActivity.this,
                                                                    NotificationTimer.class),
                                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                            alarmManager.setRepeating(
                                                    AlarmManager.RTC_WAKEUP,
                                                    System.currentTimeMillis(),
                                                    1000 * 60 * 12,
                                                    pendingIntent);
                                            progressDialog.dismiss();
                                            load();
                                        } catch (JSONException e) {
                                            //// TODO: 07-08-2017 Check URL or raise ticket
                                            progressDialog.dismiss();
                                            e.printStackTrace();
                                            editor.apply();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        //// TODO: 07-08-2017 Check URL or raise ticket
                                        if (error == null)
                                            Log.d("RESPONSE_ENTRIES", "Fatal error");
                                        Log.d("RESPONSE_ENTRIES", error.getMessage());
                                    }
                                }
                        );

                gameNoRequest.setRetryPolicy(VolleyUtils.getRetryPolicy());
                queue.add(gameNoRequest);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("QUARK_BACKEND", "Quark backend not working");
                progressDialog.dismiss();
                // TODO: 07-08-2017 Ask user to raise a github issue for backend not working
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        nodeRequest.setRetryPolicy(VolleyUtils.getRetryPolicy());

        queue.add(nodeRequest);
    }

    @OnClick(R.id.edit_button)
    public void onEditClicked() {
        enterDataLayout.setVisibility(View.VISIBLE);
        gameNameSet.setText(prefs.getString("link", ""));
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        load();
    }

    @OnClick(R.id.web_button)
    public void onWebClicked() {
        String url = prefs.getString("link", "https://hastebin.com/raw/wofebayapa");
        if (!url.startsWith("http"))
            url = "https://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
