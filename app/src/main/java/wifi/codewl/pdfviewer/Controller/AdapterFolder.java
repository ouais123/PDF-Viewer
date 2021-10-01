package wifi.codewl.pdfviewer.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wifi.codewl.pdfviewer.Kernel.Status;
import wifi.codewl.pdfviewer.Model.Folder;
import wifi.codewl.pdfviewer.R;
import wifi.codewl.pdfviewer.View.FileActivity;
import wifi.codewl.pdfviewer.View.FolderActivity;

public class AdapterFolder extends RecyclerView.Adapter<AdapterFolder.ViewHolder> {

    private Context context;
    private List<Folder> list;
    private List<Folder> listAll;
    private boolean status = false;
    private boolean first = true;
    private int count = 0;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Folder> filterProjects = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filterProjects.addAll(listAll);
            else
                for(Folder folder : listAll)
                    if(folder.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filterProjects.add(folder);
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterProjects;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Folder>) results.values);
            notifyDataSetChanged();
        }
    };


    public AdapterFolder(Context context , List<Folder> list){
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(this.list);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_folder,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Folder folder = list.get(position);
        holder.image.setImageResource(R.drawable.folder);
        holder.name.setText(folder.getName());
        holder.number.setText(folder.getNumber()+" files");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Filter getFilter() {
        return filter;
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public int removeAtFile(String path){
        for (int i = 0; i < list.size(); i++)
            if(list.get(i).getPath().equalsIgnoreCase(path))
                return i;
        return -1;
    }

    public void updateData(List<Folder> folders) {
        list.clear();
        list.addAll(folders);
        notifyDataSetChanged();
    }


    public void addAt(Folder folder){
        list.add(folder);
        notifyDataSetChanged();
    }

    public void changeAt(int position, Folder folder){
        folder.setNumber(list.get(position).getNumber());
        list.set(position,folder);
        notifyItemChanged(position);
    }


    public void clear() {
        status = false;
        first = true;
        count = 0;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FolderActivity.recyclerView.getLayoutParams();
        params.setMargins(0, 0, 0, 0); //left, top, right, bottom
        FolderActivity.recyclerView.setLayoutParams(params);
        FolderActivity.label.setVisibility(View.INVISIBLE);
        Status.chooser.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private ImageView image;
        private TextView name;
        private TextView number;
        //private boolean single;

        public ViewHolder(@NonNull final View view) {
            super(view);
            image = view.findViewById(R.id.image_folder);
            name = view.findViewById(R.id.name_folder);
            number = view.findViewById(R.id.number_files);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }


        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public boolean onLongClick(View view) {
            //if(single)
              //  return true;

            //single = true;
            status = true;
            first = true;
            count++;
            if(count > 1) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme());
                FolderActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                FolderActivity.rename.setEnabled(false);
            }
            Status.chooser.add(new File(list.get(getAdapterPosition()).getPath()));
            Status.views.add(view);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FolderActivity.recyclerView.getLayoutParams();
            params.setMargins(0, 0, 0, (int)(Resources.getSystem().getDisplayMetrics().heightPixels*0.065)); //left, top, right, bottom
            FolderActivity.recyclerView.setLayoutParams(params);

            FolderActivity.label.setVisibility(View.VISIBLE);
            view.setBackgroundColor(context.getResources().getColor(R.color.click,context.getTheme()));
            return false;
        }


        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(status){
                if(first){
                    first = false;
                    return;
                }

                int color = 0;
                Drawable background = view.getBackground();
                if (background instanceof ColorDrawable)
                    color = ((ColorDrawable) background).getColor();
                if(color == context.getResources().getColor(R.color.whit,context.getTheme())) {

                 //   if(single)
                   //     return;
                    view.setBackgroundColor(context.getResources().getColor(R.color.click, context.getTheme()));
                    //single=true;
                    count++;
                    Status.chooser.add(new File(list.get(position).getPath()));
                    Status.views.add(view);
                    if(count >1) {
                        Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme());
                        FolderActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        FolderActivity.rename.setEnabled(false);
                    }
                } else {

                    view.setBackgroundColor(context.getResources().getColor(R.color.whit, context.getTheme()));
                    //single = false;
                    count--;
                    Status.chooser.remove(new File(list.get(position).getPath()));
                    Status.views.remove(view);
                    if (count < 2) {
                        Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24,context.getTheme());
                        FolderActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        FolderActivity.rename.setEnabled(true);
                    }
                }
                if(count==0){
                    status=false;
                    first = true;
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FolderActivity.recyclerView.getLayoutParams();
                    params.setMargins(0, 0, 0, 0); //left, top, right, bottom
                    FolderActivity.recyclerView.setLayoutParams(params);
                    FolderActivity.label.setVisibility(View.INVISIBLE);
                    Status.chooser.remove(new File(list.get(position).getPath()));
                    Status.views.remove(view);
                }
                return;
            }

            Intent intent = new Intent(context, FileActivity.class);
            intent.putExtra("path",list.get(position).getPath());
            context.startActivity(intent);
        }



    }


}
