package engineering.reverse.ludumcomments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.feras.mdv.MarkdownView;

/**
 * Created by cogito on 7/31/17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CommentHolder> {
    private ArrayList<CommentData> commentDatas;
    SimpleDateFormat dateFormat;
    PrettyTime prettyTime;

    public MyRecyclerAdapter(ArrayList<CommentData> commentDatas) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        prettyTime = new PrettyTime();
        this.commentDatas = commentDatas;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
        CommentHolder c = new CommentHolder(v);
        return c;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        CommentData commentData = commentDatas.get(commentDatas.size() - 1 - position);
        if(commentData.getComment() != null)
            holder.comment.loadMarkdown(commentData.getComment());
        if(commentData.getLove() != null)
            holder.love.setText(commentData.getLove());
        if(commentData.getName() != null)
            holder.authorName.setText(commentData.getName());
        Date date = null;
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

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
