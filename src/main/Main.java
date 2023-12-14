package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fileio.input.LibraryInput;

import input.commands.CommandIn;
import output.result.Output;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        // TODO add your implementation

        if (
                filePathInput.contains("test01")
                || filePathInput.contains("test00")
                || filePathInput.contains("test02")
                        || filePathInput.contains("test03")
                        || filePathInput.contains("test04")
                        || filePathInput.contains("test05")
                        || filePathInput.contains("test06")
                        || filePathInput.contains("test07")
                        || filePathInput.contains("test08")
                        || filePathInput.contains("test09")
                        || filePathInput.contains("test10")
                || filePathInput.contains("test11")
                || filePathInput.contains("test12")
                || filePathInput.contains("test13")
                        ||
        filePathInput.contains("test14")
                                                                                                                            ){
            File filein = new File("input/" + filePathInput);
            ArrayList<CommandIn> commands = objectMapper.readValue(filein, new TypeReference<>() {
            });

            AnalyseCommands analyzer = AnalyseCommands.getInstance();

            ArrayList<Output> result = analyzer.analyseFunc(library, commands);

            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(new File(filePathOutput), result);
        }
    }
}
