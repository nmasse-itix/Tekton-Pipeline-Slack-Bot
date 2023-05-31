package fr.itix;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;
import com.slack.api.bolt.socket_mode.SocketModeApp;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.jboss.logging.Logger;

@QuarkusMain
public class SlackApp implements QuarkusApplication {
  private static final Logger LOG = Logger.getLogger(SlackApp.class);
  private int retCode = 1;

  @Override
  public int run(String... args) throws Exception {
    String pipelineId = System.getenv("TEKTON_PIPELINE_ID");
    if ("".equals(pipelineId)) {
      System.err.println("Please specify the TEKTON_PIPELINE_ID environment variable!");
      return retCode;
    }

    String channel = System.getenv("SLACK_CHANNEL");
    if ("".equals(channel)) {
      System.err.println("Please specify the SLACK_CHANNEL environment variable!");
      return retCode;
    }

    App app = new App();
    SocketModeApp smapp = new SocketModeApp(app);
    app.command("/lgtm", (req, ctx) -> {
      LOG.infov("/lgtm received on channel {0}", ctx.getChannelId());

      SlashCommandPayload payload = req.getPayload();
      String approvedPipelineId = payload.getText();

      if (approvedPipelineId == null || approvedPipelineId.trim().length() == 0) {
        return ctx.ack("Please give some pipeline id.");
      }

      if (pipelineId.equals(approvedPipelineId)) {
        Response ret = ctx.ack(res -> res.responseType("in_channel").text("Tekton pipeline " + pipelineId + " approved!"));

        // Upon confirmation send, exit (asynchronously, to let the Bolt framework performs its duty).
        Thread exitThread = new Thread(() -> {
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            LOG.debug(e);
          }

          // Gracefully terminate the app
          retCode = 0;
          Quarkus.asyncExit();
        });
        exitThread.start();

        return ret;
      }

      return ctx.ack();
    });

    smapp.startAsync();

    // Notify the channel
    app.client().chatPostMessage(req -> req
      .channel(channel)
      .text("Waiting for approval of Tekton Pipeline #" + pipelineId)
    );

    Quarkus.waitForExit();

    return retCode;
  }
}

