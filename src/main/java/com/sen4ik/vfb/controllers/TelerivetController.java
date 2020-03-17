package com.sen4ik.vfb.controllers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@Slf4j
public class TelerivetController {

    @Value("${telerivet.webhook.secret}")
    private String webHookSecret;

    @PostMapping("/telerivet")
    public void telerivet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("CALLED: telerivet()");

        PrintWriter out = response.getWriter();

        if (!webHookSecret.equals(request.getParameter("secret")))
        {
            response.setStatus(403);
            out.write("Invalid webhook secret");
            log.info("Invalid webhook secret");
        }
        else if ("incoming_message".equals(request.getParameter("event")))
        {
            String content = request.getParameter("content");
            String fromNumber = request.getParameter("from_number");
            String phoneId = request.getParameter("phone_id");

            // do something with the message, e.g. send an autoreply
            response.setContentType("application/json");

            try
            {
                JSONArray messages = new JSONArray();
                JSONObject reply = new JSONObject();
                reply.put("content", "Thanks for your message!");
                messages.put(reply);

                JSONObject json = new JSONObject();
                json.put("messages", messages);

                json.write(out);
            }
            catch (JSONException ex)
            {
                throw new ServletException(ex);
            }
        }

    }

}
