package com.studyoptimizer.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyoptimizer.domain.PlanningInput;
import com.studyoptimizer.domain.StudyPlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStore {
    private final ObjectMapper mapper;
    private final Path baseDir;

    public FileStore() {
        this.baseDir = Path.of(System.getProperty("user.home"), ".study-optimizer");
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Path inputPath() {
        return baseDir.resolve("input.json");
    }

    public Path planPath() {
        return baseDir.resolve("plan.json");
    }

    public void saveInput(PlanningInput input) throws IOException {
        ensureBaseDir();
        mapper.writeValue(inputPath().toFile(), input);
    }

    public PlanningInput loadInput() throws IOException {
        var path = inputPath();
        if (!Files.exists(path)) {
            throw new IOException("Missing input file: " + path + " (run `init` first)");
        }
        return mapper.readValue(path.toFile(), PlanningInput.class);
    }

    public void savePlan(StudyPlan plan) throws IOException {
        ensureBaseDir();
        mapper.writeValue(planPath().toFile(), plan);
    }

    public StudyPlan loadPlan() throws IOException {
        var path = planPath();
        if (!Files.exists(path)) {
            throw new IOException("Missing plan file: " + path + " (run `generate` first)");
        }
        return mapper.readValue(path.toFile(), StudyPlan.class);
    }

    private void ensureBaseDir() throws IOException {
        Files.createDirectories(baseDir);
    }
}

