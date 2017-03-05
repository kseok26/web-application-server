package controller;

import model.HttpRequest;
import model.HttpResponse;

/**
 * Created by young-seok on 2017. 3. 5..
 */
public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
