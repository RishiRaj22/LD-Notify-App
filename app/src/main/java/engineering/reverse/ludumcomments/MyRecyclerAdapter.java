package engineering.reverse.ludumcomments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cogito on 7/31/17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CommentHolder> {
    private ArrayList<CommentData> commentDatas;
    public MyRecyclerAdapter(ArrayList<CommentData> commentDatas) {
        this.commentDatas=commentDatas;
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card,parent,false);
        CommentHolder c=new CommentHolder(v);
        return c;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        CommentData commentData=commentDatas.get(commentDatas.size()-1-position);
        holder.comment.setText(commentData.comment);
        holder.love.setText(commentData.love);
        holder.time.setText(commentData.time);
    }

    @Override
    public int getItemCount() {
        return commentDatas.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        public TextView comment,love,time;
        public CommentHolder(View itemView) {
            super(itemView);
            comment=(TextView) itemView.findViewById(R.id.comment);
            love=(TextView) itemView.findViewById(R.id.love);
            time=(TextView) itemView.findViewById(R.id.time);
        }
    }
}
