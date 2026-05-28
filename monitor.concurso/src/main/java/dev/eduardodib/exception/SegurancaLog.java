package dev.eduardodib.exception;

import io.quarkus.vertx.http.runtime.filters.Filter;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

//@ApplicationScoped
//public class SegurancaLog implements Filter {

   // private static final Logger LOG = Logger.getLogger(SegurancaLog.class);

  //  @Override
    //public Handler<RoutingContext> getHandler() {
      //  return context -> {
       //     context.addBodyEndHandler(v -> {
        //        int status = context.response().getStatusCode();
        //        LOG.infof("FILTER ATIVADO: %d %s", status, context.request().path());

//                if (status == 401 || status == 403) {
  //                  String method = context.request().method().name();
    //                String path = context.request().path();
 //                   String ip = context.request().getHeader("X-Forwarded-For");
//
   //                  if (ip == null || ip.isEmpty()) {
     //                   ip = context.request().remoteAddress().host();
       //             }

         //           LOG.warnf("[SEGURANÇA] %d | Método: %s | Endpoint: %s | IP: %s",
           //                 status, method, path, ip);
            //    }
           // });
           // context.next();
        //};
    //}

    //@Override
   // public int getPriority() {
      //  return 100;
  //  }

