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
import wifi.codewl.pdfviewer.Model.PDF;
import wifi.codewl.pdfviewer.R;
import wifi.codewl.pdfviewer.View.FileActivity;
import wifi.codewl.pdfviewer.View.PDFActivity;

public class AdapterFile extends RecyclerView.Adapter<AdapterFile.ViewHolder> {
    private Context context;
    private List<PDF> list;
    private List<PDF> listAll;
    private boolean status = false;
    private boolean first = true;
    private int count = 0;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PDF> filterProjects = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filterProjects.addAll(listAll);
            else
                for(PDF pdf : listAll)
                    if(pdf.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filterProjects.add(pdf);
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterProjects;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends PDF>) results.values);
            notifyDataSetChanged();
        }
    };

    public AdapterFile(Context context ,List<PDF> list){
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(this.list);
    }

    public List<PDF> getList(){
        return list;
    }

    @NonNull
    @Override
    public AdapterFile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFile.ViewHolder holder, int position) {
        PDF pdf = list.get(position);
        holder.image.setImageResource(R.drawable.file);
        holder.name.setText(pdf.getName().substring(0,pdf.getName().length()-4));
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
        notifyItemRangeChanged(position, list.size() );
    }

    public int removeAtFile(String path){
        for (int i = 0; i < list.size(); i++)
            if(list.get(i).getPath().equalsIgnoreCase(path))
                return i;
        return -1;
    }


    public void updateData(List<PDF> pdfs) {
        list.clear();
        list.addAll(pdfs);
        notifyDataSetChanged();
    }


    public void addAt(PDF pdf){
        list.add(pdf);
        notifyDataSetChanged();
    }

    public void changeAt(int position, PDF pdf){
        list.set(position,pdf);
        notifyItemChanged(position);
    }





    public void clear() {
        status = false;
        first = true;
        count = 0;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FileActivity.recyclerView.getLayoutParams();
        params.setMargins(0, 0, 0, 0); //left, top, right, bottom
        FileActivity.recyclerView.setLayoutParams(params);
        FileActivity.label.setVisibility(View.INVISIBLE);
        Status.chooser.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,View.OnLongClickListener{

        public ImageView image;
        public TextView name;

        //private boolean single;
        public ViewHolder(@NonNull final View view) {
            super(view);
            image = view.findViewById(R.id.image_file);
            name = view.findViewById(R.id.name_file);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);

        }


        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View view) {
            int position  =getAdapterPosition();
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
                    //if(single)
                      //  return;
                    view.setBackgroundColor(context.getResources().getColor(R.color.click, context.getTheme()));
                    Status.chooser.add(new File(list.get(position).getPath()));
                    Status.views.add(view);
                  //  single=true;
                    count++;
                    if(count >1) {
                        //FileActivity.rename.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme()));
                        Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme());
                        FileActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        FileActivity.rename.setEnabled(false);
                    }
                }
                else {
                    view.setBackgroundColor(context.getResources().getColor(R.color.whit, context.getTheme()));
                //    single = false;
                    count--;
                    Status.chooser.remove(new File(list.get(position).getPath()));
                    Status.views.remove(view);
                    if (count < 2) {
                        //FileActivity.rename.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_create_24,context.getTheme()));
                        Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24,context.getTheme());
                        FileActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        FileActivity.rename.setEnabled(true);
                    }
                }
                if(count==0){
                    status=false;
                    first = true;
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FileActivity.recyclerView.getLayoutParams();
                    params.setMargins(0, 0, 0, 0); //left, top, right, bottom
                    FileActivity.recyclerView.setLayoutParams(params);
                    FileActivity.label.setVisibility(View.INVISIBLE);
                    Status.chooser.remove(new File(list.get(position).getPath()));
                    Status.views.remove(view);
                }
                return;
            }
            Intent intent = new Intent(context, PDFActivity.class);
            intent.putExtra("path",list.get(position).getPath());
            Status.currentPage = 0;
            context.startActivity(intent);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public boolean onLongClick(View view) {
           // if(single) {
             //   return true;
            //}
            //single = true;
            status = true;
            first = true;
            count++;
            if(count > 1) {
                //FileActivity.rename.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme()));
                Drawable img = context.getResources().getDrawable(R.drawable.ic_baseline_create_24_disaple,context.getTheme());
                FileActivity.rename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                FileActivity.rename.setEnabled(false);
            }
            Status.chooser.add(new File(list.get(getAdapterPosition()).getPath()));
            Status.views.add(view);
            FileActivity.label.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) FileActivity.recyclerView.getLayoutParams();
            params.setMargins(0, 0, 0, (int)(Resources.getSystem().getDisplayMetrics().heightPixels*0.065)); //left, top, right, bottom
            FileActivity.recyclerView.setLayoutParams(params);
            view.setBackgroundColor(context.getResources().getColor(R.color.click,context.getTheme()));
            return false;

        }
    }
}


