package com.usvajanjepasa.usvajanjepasa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {
    List<ModelComment> commentList;
    Context context;

    public AdapterComments(Context context2, List<ModelComment> commentList2) {
        this.context = context2;
        this.commentList = commentList2;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(this.context).inflate(R.layout.comments, parent, false));
    }

    public void onBindViewHolder(MyHolder holder, int position) {
        commentList.get(position).getComID();
        String comment = this.commentList.get(position).getComment();
        commentList.get(position).getCurrUid();
        String usersName = this.commentList.get(position).getCurrName();
        commentList.get(position).getCurrEmail();
        holder.nameTV.setText(usersName);
        holder.commentTV.setText(comment);
    }

    @Override
    public int getItemCount() {
        return this.commentList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView commentTV;
        TextView nameTV;

        public MyHolder(View itemView) {
            super(itemView);
            this.nameTV = (TextView) itemView.findViewById(R.id.commNameTV);
            this.commentTV = (TextView) itemView.findViewById(R.id.commentTV);
        }
    }
}
