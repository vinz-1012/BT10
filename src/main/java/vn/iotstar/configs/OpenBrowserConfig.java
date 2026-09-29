package vn.iotstar.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

@Component
public class OpenBrowserConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenBrowserConfig.class);

    @Value("${server.port:8080}")
    private String port;

    @Value("${app.open-browser:true}")
    private boolean openBrowser;

    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser() {
        if (!openBrowser) {
            return;
        }

        String url = "http://localhost:" + port + "/auth/login";
        log.info(">>> Ung dung da khoi dong thanh cong! Dang tu dong mo trinh duyet tai: {}", url);

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                openBrowserOsSpecific(os, url);
            }
        } catch (Exception e) {
            openBrowserOsSpecific(os, url);
        }
    }

    private void openBrowserOsSpecific(String os, String url) {
        ProcessBuilder processBuilder;

        try {
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", url);
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("xdg-open", url);
            } else {
                log.warn("He dieu hanh khong duoc ho tro de tu dong mo trinh duyet.");
                return;
            }

            processBuilder.start();
        } catch (IOException e) {
            log.warn("Khong the tu dong bat trinh duyet: {}", e.getMessage());
        }
    }
}
