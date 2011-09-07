package kg.apc.perfmon;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ListIterator;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author undera
 */
public class PerfMonAgentTool {

    private static final Logger log = LoggingManager.getLoggerForClass();

    protected int processParams(ListIterator args) throws UnsupportedOperationException, IllegalArgumentException {
        PerfMonWorker worker;
        try {
            worker = getWorker();
        } catch (IOException ex) {
            log.error("Error", ex);
            return 0;
        }

        while (args.hasNext()) {
            String nextArg = (String) args.next();
            log.debug("Arg: " + nextArg);
            if (nextArg.equalsIgnoreCase("--tcp-port")) {
                if (!args.hasNext()) {
                    throw new IllegalArgumentException("Missing TCP Port no");
                }

                worker.setTCPPort(Integer.parseInt((String) args.next()));
            } else if (nextArg.equalsIgnoreCase("--metrics")) {
                if (!args.hasNext()) {
                    throw new IllegalArgumentException("Missing metrics specification");
                }

                worker.setMetrics((String) args.next());
            } else if (nextArg.equalsIgnoreCase("--udp-port")) {
                if (!args.hasNext()) {
                    throw new IllegalArgumentException("Missing UDP Port no");
                }

                worker.setUDPPort(Integer.parseInt((String) args.next()));
            } else {
                throw new UnsupportedOperationException("Unrecognized option: " + nextArg);
            }
        }

        try {
            worker.startAcceptingCommands();

            while (!worker.isFinished()) {
                worker.processCommands();
            }
        } catch (IOException e) {
            log.error("Error", e);
            return 0;
        }

        return worker.getExitCode();
    }

    protected void showHelp(PrintStream os) {
        os.println("Options for tool 'PerfMon': "
                + "[ --tcp-port <port no> "
                + "--udp-port <port no> ]");
    }

    protected PerfMonWorker getWorker() throws IOException {
        return new PerfMonWorker();
    }
}