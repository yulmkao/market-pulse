package academy.backend.market_pulse.repository.store.impl;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {

    @Override
    public void save(Path path, List<Quote> quotes) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (Quote quote : quotes) {
                    Instrument instrument = quote.getInstrument();
                    String line = instrument.getTicker() + ";"
                            + instrument.getType() + ";"
                            + instrument.getName() + ";"
                            + instrument.getCurrency() + ";"
                            + quote.getPrice() + ";"
                            + quote.getChangePercent();
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("ошибка при сохранении котировок", e);
        }
    }


    @Override
    public List<Quote> load(Path path) {
        List<Quote> quotes = new ArrayList<>();
        if (path == null || Files.notExists(path)) {
            return quotes;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                String ticker = parts[0];
                String type = parts[1];
                String name = parts[2];
                Currency currency = Currency.valueOf(parts[3]);
                BigDecimal price = new BigDecimal(parts[4]);
                BigDecimal changePercent = new BigDecimal(parts[5]);
                Instrument instrument = InstrumentFactories.create(type, ticker, name, currency);
                Quote quote = new Quote(instrument, price, changePercent);
                quotes.add(quote);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return quotes;
    }
}