package com.ymedialabs.githubfollowers.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ymedialabs.githubfollowers.R;
import com.ymedialabs.githubfollowers.models.User;
import com.ymedialabs.githubfollowers.uiutils.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private static final String TAG = UserAdapter.class.getSimpleName();
    private final Context context;
    private final ArrayList<User> userList;
    private final ImageLoader imgLoader;
    private CustomOnClickListener customOnClickListener;

    public interface CustomOnClickListener{

        public void onItemClick(View view,int position,String loginName);
    }

    public void setClickListener(CustomOnClickListener customOnClickListener) {
        this.customOnClickListener = customOnClickListener;
    }
    
    public UserAdapter(Context context, ArrayList<User> userList){
        
        this.context = context;
        this.userList = userList;

        imgLoader = new ImageLoader(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View userView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_card,parent,false);
        return new ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final User user = userList.get(position);
        holder.tvName.setText(user.getLoginName());
     //   Picasso.with(context).load(user.getAvatarUrl()).placeholder(R.drawable.ic_face_black_24dp).error(R.drawable.ic_face_black_24dp).into(holder.ivAvtar);

        /*final BitmapManager bitmapManager = BitmapManager.getInstance();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            Bitmap bitmap=null;
            @Override
            public void run() {
                bitmap = bitmapManager.downloadBitmap(user.getAvatarUrl(), 100, 100);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(bitmap!=null)
                            holder.ivAvtar.setImageBitmap(bitmap);
                    }
                });
            }
        };

        Thread threadRunnable = new Thread(runnable);
        threadRunnable.start();*/
        imgLoader.displayImage(user.getAvatarUrl(),holder.ivAvtar);
    }

    @Override
    public int getItemCount() {

        if(userList!=null)
             return userList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public CircleImageView ivAvtar;

        public ViewHolder(View view) {

            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivAvtar = (CircleImageView) view.findViewById(R.id.ivAvtar);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    User user = userList.get(position);
                    String loginName = "";
                    if (user != null) {
                        loginName = user.getLoginName();
                        if (position != RecyclerView.NO_POSITION)
                            customOnClickListener.onItemClick(view, position,loginName);
                    }else{
                        Toast.makeText(context,context.getString(R.string.no_user_info),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
