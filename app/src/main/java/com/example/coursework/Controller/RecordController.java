package com.example.coursework.Controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.EditRecord;
import com.example.coursework.R;

import java.util.List;

public class RecordController extends RecyclerView.Adapter<RecordController.ViewHolder> {

    private Context context;

    private List<ModelRecords> list;

    public RecordController(Context context, List<ModelRecords> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecordController.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordController.ViewHolder holder, int position) {
        ModelRecords record = list.get(position);

        holder.tvDate.setText(record.date);
        holder.tvAddress.setText(record.adress);
        holder.tvViolation.setText(record.narushenia_name);
        holder.tvPassport.setText("Паспорт: " + record.passport);
        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRecord.class);
            intent.putExtra("record_id", position);
            context.startActivity(intent);
        });


        if (record.coment == null || record.coment.isEmpty()) {
            holder.tvComment.setVisibility(View.GONE);
        } else {
            holder.tvComment.setVisibility(View.VISIBLE);
            holder.tvComment.setText(record.coment);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAddress, tvViolation,  tvPassport,tvComment;
        ImageButton edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvViolation = itemView.findViewById(R.id. tvViolation);
            tvPassport = itemView.findViewById(R.id.tvPassport);
            tvComment = itemView.findViewById(R.id.tvComment);
            edit = itemView.findViewById(R.id.buttonEdit);


        }
    }
}
