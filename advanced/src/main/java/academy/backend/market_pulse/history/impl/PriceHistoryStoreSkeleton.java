package academy.backend.market_pulse.history.impl;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            Files.createDirectories(directory);
            Path path = Files.createTempFile(directory, "point-", ".txt");
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(point.ticker() + ";" + point.timestamp() + ";" + point.price());
            }
            return path;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        List<PricePoint> points = new ArrayList<>();
        if (Files.notExists(directory) || !Files.isDirectory(directory)) {
            return points;
        }
        try (Stream<Path> files = Files.list(directory)) {
            points = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("point-"))
                    .map(this::readPoint)
                    .filter(point -> point.ticker().equals(ticker))
                    .collect(Collectors.toList());
            points.sort(Comparator.comparing(PricePoint::timestamp));
            return points;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PricePoint readPoint(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            String[] parts = lines.get(0).split(";");
            String ticker = parts[0];
            Instant timestamp = Instant.parse(parts[1]);
            BigDecimal price = new BigDecimal(parts[2]);
            return new PricePoint(ticker, timestamp, price);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}