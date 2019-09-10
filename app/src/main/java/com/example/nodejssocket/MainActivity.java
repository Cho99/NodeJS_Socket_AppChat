package com.example.nodejssocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ListView listUser, listChat;
    EditText edtContent;
    ImageButton btnAdd, btnSend;

    ArrayList<String> arrayUser;
    ArrayAdapter adapter;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSocket = IO.socket("http://192.168.1.3:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        mSocket.on("server-send-result", onRetrieveResult);
        mSocket.on("server-send-users", onListUsers);

        AnhXa();
       arrayUser = new ArrayList<>();
       adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayUser);
       listUser.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtContent.getText().toString().trim().length() > 0){
                    mSocket.emit("client-register-user", edtContent.getText().toString());
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtContent.getText().toString().trim().length() > 0) {
                    mSocket.emit("client-send-chat", edtContent.getText().toString());
                }
            }
        });


    }

    public void AnhXa() {
        btnAdd = (ImageButton) findViewById(R.id.imgAdd);
        btnSend = (ImageButton) findViewById(R.id.imgSend);
        listUser = (ListView) findViewById(R.id.listViewUser);
        listChat = (ListView) findViewById(R.id.listviewChat);
        edtContent = (EditText) findViewById(R.id.editTextContent);
    }

    private Emitter.Listener onListUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("danhsach");
                        arrayUser.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String username = jsonArray.getString(i);
                            arrayUser.add(username);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onRetrieveResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        boolean exsist = jsonObject.getBoolean("ketqua");
                        if (exsist) {
                            Toast.makeText(MainActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
