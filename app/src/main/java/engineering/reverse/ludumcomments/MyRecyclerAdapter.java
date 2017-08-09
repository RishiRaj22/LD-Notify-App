package engineering.reverse.ludumcomments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.feras.mdv.MarkdownView;

/**
 * Created by cogito on 7/31/17.
 */

class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CommentHolder> {
    private SimpleDateFormat dateFormat;
    private PrettyTime prettyTime;
    private ArrayList<CommentData> commentDatas;

    MyRecyclerAdapter(ArrayList<CommentData> commentDatas) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        prettyTime = new PrettyTime();
        this.commentDatas = commentDatas;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        position = holder.getAdapterPosition();
        holder.bodyExpandButton.setVisibility(View.INVISIBLE);
        holder.authorBody.setVisibility(View.GONE);
        CommentData commentData = commentDatas.get(commentDatas.size() - 1 - position);
        if (commentData.getComment() != null)
            holder.comment.loadMarkdown(commentData.getComment());
        if (commentData.getLove() != null)
            holder.love.setText(commentData.getLove());
        if (commentData.getName() != null) {
            holder.authorName.setText(commentData.getName());
        }
        if (commentData.getBody() != null && commentData.getBody().length() > 0) {
            holder.authorBody.loadMarkdown("## About " + commentData.getName() + "\n" + commentData.getBody(), "file:///android_asset/style.css");
            holder.bodyExpandButton.setVisibility(View.VISIBLE);
        }
        Date date;
        try {
            date = dateFormat.parse(commentData.getTime());
            holder.time.setText(prettyTime.format(date));
        } catch (ParseException e) {
            holder.time.setText(commentData.getTime());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return commentDatas.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_name)
        TextView authorName;
        @BindView(R.id.comment)
        MarkdownView comment;
        @BindView(R.id.love)
        TextView love;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.expand_author)
        ImageButton bodyExpandButton;
        @BindView(R.id.body_author)
        MarkdownView authorBody;
        boolean expanded;
        Context context;

        CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        @OnClick(R.id.expand_author)
        public void onExpandClicked() {
            expanded = !expanded;
            if (expanded) authorBody.setVisibility(View.VISIBLE);
            else authorBody.setVisibility(View.GONE);
        }

        @OnClick(R.id.author_metadata)
        public void onNameClicked() {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commentDatas.get(commentDatas.size() - 1 - getAdapterPosition()).getPath()));
            context.startActivity(browserIntent);
        }
    }
}
