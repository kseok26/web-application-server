package controller;

import model.HttpRequest;
import model.HttpResponse;
import org.apache.commons.lang.StringUtils;

/**
 * Created by young-seok on 2017. 3. 5..
 */
public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (StringUtils.equals(request.getMethod(), "GET")) {
            doGet(request, response);
            return;
        }

        doPost(request, response);
    }

    public void doGet(HttpRequest request, HttpResponse response) {

    }

    public void doPost(HttpRequest request, HttpResponse response) {

    }
}
