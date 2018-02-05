package io.aloketewary.bloodconnect.fragment.pages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.aloketewary.bloodconnect.R;

/**
 * Created by AlokeT on 2/5/2018.
 */

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages>  mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();

        String currentUser = mAuth.getCurrentUser().getUid();

        Messages m = mMessageList.get(position);
        String fromUser = m.getFrom();
        if(fromUser.equals(currentUser)) {
            holder.messageAltText.setVisibility(View.VISIBLE);
            // holder.messageText.setTextColor(Color.WHITE);
            // holder.messageText.setGravity();
            holder.messageText.setVisibility(View.GONE);
            holder.profileImage.setVisibility(View.GONE);
            holder.messageAltText.setText(m.getMessage());
        } else {
            holder.messageText.setVisibility(View.VISIBLE);
            holder.profileImage.setVisibility(View.VISIBLE);
            holder.messageAltText.setVisibility(View.GONE);
            holder.messageText.setText(m.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
       public TextView messageText;
       public CircleImageView profileImage;
       public TextView messageAltText;

       public MessageViewHolder(View view) {
           super(view);
           messageText = (TextView)view.findViewById(R.id.message_single_text_layout);
           profileImage = (CircleImageView) view.findViewById(R.id.message_single_avatar);
           messageAltText = (TextView) view.findViewById(R.id.message_single_text_alt_layout);
       }
    }

}
