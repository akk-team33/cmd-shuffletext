package de.team33.cmds.shuffletext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Main {

    private static final int MAX_LINE_LENGTH = 80;
    private static final int MIN_SENTENCE_MAX = 3;
    private static final int MAX_SENTENCE_MAX = 7;

    private final Random random = new Random();
    private final String[] words;
    private final String[] puncts;
    private final int maxWords;
    private final int maxLines;

    private Main(final String[] args) throws IOException {
        final String input = read(Paths.get(args[0]).toAbsolutePath().normalize());
        this.words = Stream.of(input.split("[\\s]+"))
                .map(value -> value.replaceAll("^\\p{Punct}+", "").replaceAll("\\p{Punct}+$", ""))
                .filter(value -> 0 < value.length())
                .toArray(String[]::new);
        this.puncts = Stream.of(input.split("[^\\s.,:;-]"))
                .map(String::trim)
                .filter(value -> 0 < value.length())
                .toArray(String[]::new);
        this.maxWords = words.length;
        this.maxLines = words.length;
    }

    public static void main(final String[] args) throws IOException {
        if (1 == args.length) {
            new Main(args).run();
        } else {
            System.out
                    .println("parameter required: INPUT_TEXT_FILE");
        }
    }

    private void run() {
        final List<String> lines = new LinkedList<>();
        final StringBuilder line = new StringBuilder();
        int wordIndex = 0;
        int lineIndex = 0;
        int sentenceMax = nextSentenceMax();
        int sentenceIndex = 0;
        while (wordIndex < maxWords && lineIndex < maxLines) {
            wordIndex += 1;
            sentenceIndex += 1;
            final String nextPunct;
            if (sentenceIndex < sentenceMax)
                nextPunct = "";
            else {
                nextPunct = nextPunct();
                sentenceIndex = 0;
            }
            final String nextWord = nextWord() + nextPunct;
            if ((line.length() + nextWord.length()) > MAX_LINE_LENGTH) {
                lines.add(line.substring(1));
                line.setLength(0);
                lineIndex += 1;
            }
            line.append(" ").append(nextWord);
        }
        lines.forEach(System.out::println);
    }

    private String nextWord() {
        return words[random.nextInt(words.length)];
    }

    private String nextPunct() {
        return puncts[random.nextInt(puncts.length)];
    }

    private int nextSentenceMax() {
        return MIN_SENTENCE_MAX + random.nextInt(MAX_SENTENCE_MAX - MIN_SENTENCE_MAX);
    }

    private static String read(final Path arg) throws IOException {
        return new String(Files.readAllBytes(arg), Charset.forName("UTF-8"));
    }
}
