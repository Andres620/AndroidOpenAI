package com.andresparra.androidopenai;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;

public class MainActivity extends AppCompatActivity {

    private String prompt;
    private EditText inputQuery;
    private TextView result;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputQuery = findViewById(R.id.inputQuery);
        btnSearch = findViewById(R.id.btnSearch);
        result = findViewById(R.id.result);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prompt = inputQuery.getText().toString();
                sendPrompt(prompt);
            }
        });
    }

    public void sendPrompt(String prompt){
        String API_KEY = "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";

        URLComponents urlComp = new URLComponents();

        urlComp.setScheme("https");
        urlComp.setHost("api.openai.com");
        urlComp.setPath("/v1/chat/completions");

        URLRequest request = new URLRequest(urlComp.getURL());
        request.setHttpMethod("POST");
        request.addValue("Content-Type", "application/json");
        request.addValue("Authorization", "Bearer " + API_KEY);
        String requestBody = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}],\"temperature\":0.7}";
        request.setHttpBody(requestBody);

        URLSession.getShared().dataTask(request, (data, response, error) ->{
            HTTPURLResponse resp = (HTTPURLResponse) response;
            if (error == null){
                switch (resp.getStatusCode()){
                    case 200: //ok
                        Log.d("RESPONSE_1", prompt);
                        System.out.println("data_1" + data.toText());

                        Gson gson = new Gson();
                        Root root = gson.fromJson(data.toText(), Root.class);
                        //mostrar peticion en un hilo
                        runOnUiThread(()->{
                            showResult(root);
                        });
                        break;
                    case 404: //Not found
                        Log.d("Message ", "Server error " + resp.getStatusCode());
                        break;
                }
            }else{
                //Conecction error
                Log.d("Message", "Network error");
            }
        }).resume();
    }

    public void showResult(Root root){
        if(root != null){
            if (root.choices.size() > 0){
                Choice choices = root.choices.get(0);
                Log.d("Message", choices.message.content);
                result.setText(choices.message.content);
            }
        }
    }

}