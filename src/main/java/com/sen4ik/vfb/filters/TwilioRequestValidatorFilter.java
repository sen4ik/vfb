package com.sen4ik.vfb.filters;

import com.twilio.security.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TwilioRequestValidatorFilter implements Filter {

    private String AUTH_TOKEN;
    private RequestValidator requestValidator;

    @Autowired
    public TwilioRequestValidatorFilter(String AUTH_TOKEN){
        this.AUTH_TOKEN = AUTH_TOKEN;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        requestValidator = new RequestValidator(AUTH_TOKEN);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean isValidRequest = false;
        if (request instanceof HttpServletRequest) {
            log.info("====================> working on request");
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Concatenates the request URL with the query string
            String pathAndQueryUrl = getRequestUrlAndQueryString(httpRequest);
            log.info("pathAndQueryUrl: " + pathAndQueryUrl);

            // Extracts only the POST parameters and converts the parameters Map type
            Map<String, String> postParams = extractPostParams(httpRequest);
            log.info("postParams: " + postParams.toString());
            String signatureHeader = httpRequest.getHeader("X-Twilio-Signature");
            log.info("signatureHeader: " + signatureHeader);

            isValidRequest = requestValidator.validate(
                    pathAndQueryUrl,
                    postParams,
                    signatureHeader);
            log.info("isValidRequest: " + isValidRequest);
        }

        log.info("isValidRequest: " + isValidRequest);

        if(isValidRequest) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    private Map<String, String> extractPostParams(HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, String[]> requestParams = request.getParameterMap();
        List<String> queryStringKeys = getQueryStringKeys(queryString);

        return requestParams.entrySet().stream()
                .filter(e -> !queryStringKeys.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0]));
    }

    private List<String> getQueryStringKeys(String queryString) {
        if(queryString == null || queryString.length() == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(queryString.split("&"))
                    .map(pair -> pair.split("=")[0])
                    .collect(Collectors.toList());
        }
    }

    private String getRequestUrlAndQueryString(HttpServletRequest request) {

        // Scheme below is not https, isSecure is not true. This behavior can happen if you have a load-balancer
        // (or using reverse proxy) in front of the application. Even though requests are done in HTTPS the
        // load-balancer will reissue them as plain http requests which produce this behavior.
        // log.info("scheme: " + request.getScheme());
        // log.info("isSecured: " + request.isSecure());
        // Solution is to have X-Forwarded-Proto in nginx.conf.
        // For some reason X-Forwarded-Proto results too many redirects issue. I was tired and added custom header.
        // String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        // log.info("xForwardedProto: " + xForwardedProto);

        String queryString = request.getQueryString();
        String requestUrl = request.getRequestURL().toString();
        String scheme = request.getHeader("X-WhatsMyScheme");
        if(!requestUrl.startsWith(scheme)){
            requestUrl = requestUrl.replace("http://", "https://");
        }

        if(queryString != null && queryString != "") {
            return requestUrl + "?" + queryString;
        }
        return requestUrl;
    }
}
