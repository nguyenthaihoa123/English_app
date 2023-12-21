package com.example.englishapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Vocab;
import com.example.englishapp.viewmodel.VocabVM;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder> {
    private List<Vocab> vocabList;
    private Context context;
    TextToSpeech t1;
    private OnVocabDeleteListener onVocabDeleteListener;

    // Constructor nhận danh sách từ vựng
    public VocabAdapter(Context context, List<Vocab> vocabList) {
        this.context = context;
        this.vocabList = vocabList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một ViewHolder từ layout item_vocab
        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy từ vựng tại vị trí position
        Vocab vocab = vocabList.get(position);

        // Đổ dữ liệu từ vựng vào ViewHolder
        holder.txtName.setText(vocab.getWord());
        holder.txtTranslation.setText(vocab.getTrans());

        t1=new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        holder.btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.speak(vocab.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        holder.item_Vocab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa từ");
                builder.setMessage("Bạn có chắc muốn xóa từ này?");

                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa từ vựng
                        VocabVM deleteVM = new VocabVM();
                        deleteVM.deleteVocab(vocab.getIdTopic(), vocab.getId());
                        if (onVocabDeleteListener != null) {
                            onVocabDeleteListener.onVocabDeleted(position);
                        }
                        // Cập nhật RecyclerView


                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng từ vựng trong danh sách
        return vocabList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các View trong layout item_vocab
        TextView txtName, txtTranslation;
        ImageButton btnSound;
        LinearLayout item_Vocab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các View
            txtName = itemView.findViewById(R.id.txt_Name_Vocab);
            txtTranslation = itemView.findViewById(R.id.txt_trans_vocab);
            btnSound = itemView.findViewById(R.id.btn_sound_vocab);
            item_Vocab = itemView.findViewById(R.id.layout_vocab_item);
        }
    }


    public void filterList(List<Vocab> filteredList) {
        vocabList = filteredList;
        notifyDataSetChanged();
    }
    public void update(){
        vocabList = vocabList;
        notifyDataSetChanged();
    }
    public interface OnVocabDeleteListener {
        void onVocabDeleted(int position);
    }
    public void setOnVocabDeleteListener(OnVocabDeleteListener listener) {
        this.onVocabDeleteListener = listener;
    }

}
