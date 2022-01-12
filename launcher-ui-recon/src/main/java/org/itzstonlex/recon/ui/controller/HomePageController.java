package org.itzstonlex.recon.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.itzstonlex.recon.metrics.MetricCounter;
import org.itzstonlex.recon.metrics.MetricTimeSnippet;
import org.itzstonlex.recon.metrics.ReconMetrics;
import org.itzstonlex.recon.ui.scheduler.TaskScheduler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class HomePageController extends AbstractPageController {

    @FXML
    private MenuItem help_openDiscord;

    @FXML
    private MenuItem help_openVkontakte;


    @FXML
    private MenuItem github_openRepository;

    @FXML
    private MenuItem github_openModuleDocs;


    @FXML
    private Label usedMemoryProgress;

    @FXML
    private Label usedMemory;

    @FXML
    private ProgressBar memoryProgress;


    @FXML
    private GridPane runningThreadsPane;

    @FXML
    private GridPane graphicsGridPane;


    @Override
    public void initialize() {
        addHelpMenuActions();
        addGithubMenuActions();

        startThreadsUpdater();
        startGraphicsUpdater();

        startMemoriesUpdater(Runtime.getRuntime());
    }


    private final String threadTitleFormat = "   Thread@%s";

    private void drawThreads() {
        Platform.runLater(() -> runningThreadsPane.getChildren().clear());

        Thread.getAllStackTraces().keySet().forEach(new Consumer<Thread>() {
            private int counter = 0;

            @Override
            public void accept(Thread thread) {
                if (thread.getName().startsWith("recon")) {
                    String threadTitle = String.format(threadTitleFormat, thread.getName());

                    Label label = new Label(threadTitle);

                    if (label.getFont().getSize() != 16) {
                        label.setFont(new Font(16));
                    }

                    switch (thread.getState()) {
                        case NEW: {
                            label.setTextFill(Color.LIME);
                            break;
                        }

                        case BLOCKED: {
                            label.setTextFill(Color.RED);
                            break;
                        }

                        case WAITING:
                        case TIMED_WAITING: {
                            label.setTextFill(Color.ORANGE);
                            break;
                        }

                        case TERMINATED: {
                            label.setTextFill(Color.DARKRED);
                            break;
                        }

                        case RUNNABLE: {
                            label.setTextFill(Color.GREEN);
                            break;
                        }
                    }

                    Platform.runLater(() -> runningThreadsPane.add(label, 0, counter++));
                }
            }
        });
    }

    private void startThreadsUpdater() {
        new TaskScheduler("running_threads_recon") {

            @Override
            public void run() {
                drawThreads();
            }

        }.runTimer(0, 1000, TimeUnit.MILLISECONDS);
    }


    private final Map<Integer, LineChart<String, Number>> graphicsMap
            = new HashMap<>();

    private void updateGraphicNode(MetricCounter metricCounter,
                                   XYChart.Series<String, Number> series) {
        Platform.runLater(() -> {

            series.getData().clear();
            metricCounter.timeKeys()
                    .stream()
                    .sorted(Collections.reverseOrder(Comparator.comparingLong(MetricTimeSnippet::toMillis)))
                    .forEach(timeSnippet -> {

                        String value = timeSnippet.getTime() + Character.toString(timeSnippet.getUnit().name().charAt(0)).toLowerCase();
                        XYChart.Data<String, Number> data = new XYChart.Data<>(value, metricCounter.valueOf(timeSnippet));

                        series.getData().add(data);
                    });
        });
    }

    private void updateGraphic(int row, MetricCounter metricCounter) {
        LineChart<String, Number> lineChart = graphicsMap.get(row);

        XYChart.Series<String, Number> series = lineChart.getData().get(0);

        updateGraphicNode(metricCounter, series);
    }

    private void createGraphics(int row, String title, MetricCounter metricCounter) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        updateGraphicNode(metricCounter, series);

        lineChart.getData().add(series);

        graphicsGridPane.add(lineChart, 0, row);
        graphicsMap.put(row, lineChart);
    }

    private void drawGraphicCharts(boolean isCreated) {

        if (!isCreated) {
            createGraphics(0, "Read", ReconMetrics.TOTAL_BYTES_READ);
            createGraphics(1, "Write", ReconMetrics.TOTAL_BYTES_WRITE);
            createGraphics(2, "Bytes", ReconMetrics.TOTAL_BYTES);
            createGraphics(3, "Clients", ReconMetrics.TOTAL_CLIENTS);

        } else {

            updateGraphic(0, ReconMetrics.TOTAL_BYTES_READ);
            updateGraphic(1, ReconMetrics.TOTAL_BYTES_WRITE);
            updateGraphic(2, ReconMetrics.TOTAL_BYTES);
            updateGraphic(3, ReconMetrics.TOTAL_CLIENTS);
        }
    }

    private void startGraphicsUpdater() {
        new TaskScheduler("graphics_updater_recon") {
            private boolean created;

            @Override
            public void run() {
                drawGraphicCharts(created);

                this.created = true;
            }

        }.runTimer(0, 10, TimeUnit.SECONDS);
    }

    private void updateMemoryStatus(Runtime runtime) {
        memoryProgress.setProgress((double) runtime.totalMemory() / runtime.maxMemory());

        Platform.runLater(() -> {

            usedMemory.setText("Memory used: [" + (runtime.totalMemory() / 1024 / 1024) + " MB / " + (runtime.maxMemory() / 1024 / 1024) + " MB]");
            usedMemoryProgress.setText(Math.ceil((double)runtime.totalMemory() / runtime.maxMemory() * 100) + "%");
        });
    }

    private void startMemoriesUpdater(Runtime runtime) {
        new TaskScheduler("memory_updater_recon") {

            @Override
            public void run() {
                updateMemoryStatus(runtime);
            }

        }.runTimer(0, 1, TimeUnit.SECONDS);
    }

    private void addHelpMenuActions() {
        Desktop desktop = Desktop.getDesktop();

        if (!(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))) {
            return;
        }

        help_openDiscord.setOnAction(event -> {
            try {
                desktop.browse(new URI("https://discord.gg/GmT9pUy8af"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });

        help_openVkontakte.setOnAction(event -> {
            try {
                desktop.browse(new URI("https://vk.com/itzstonlex"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void addGithubMenuActions() {
        Desktop desktop = Desktop.getDesktop();

        if (!(Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))) {
            return;
        }

        github_openRepository.setOnAction(event -> {
            try {
                desktop.browse(new URI("http://github.com/ItzStonlex/Recon"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });

        github_openModuleDocs.setOnAction(event -> {
            try {
                desktop.browse(new URI("http://github.com/ItzStonlex/Recon/tree/main/launcher-ui-recon"));

            } catch (IOException | URISyntaxException exception) {
                exception.printStackTrace();
            }
        });
    }

}
