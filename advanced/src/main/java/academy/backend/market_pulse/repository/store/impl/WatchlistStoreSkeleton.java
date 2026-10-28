package academy.backend.market_pulse.repository.store.impl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, построчный текст и т.д.); главное,
 * чтобы {@code load(save(tickers))} возвращал тот же набор тикеров в том же порядке. Проверьте себя
 * тестом {@code WatchlistStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class WatchlistStoreSkeleton implements WatchlistStore {

    @Override
    public void save(Path path, Set<String> tickers) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (String ticker : tickers) {
                    writer.write(ticker);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        Set<String> tickers = new LinkedHashSet<>();
        if (path == null || Files.notExists(path)) {
            return tickers;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    tickers.add(line);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return tickers;
    }
}
