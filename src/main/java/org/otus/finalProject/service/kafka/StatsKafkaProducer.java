package org.otus.finalProject.service.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.otus.finalProject.dto.stats.PlayerStatResponse;
import org.otus.finalProject.dto.stats.TeamStatResponse;
import org.otus.finalProject.dto.stats.TopScorersStatResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StatsKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StatsKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTeamStats(TeamStatResponse payload) {
        ProducerRecord<String, Object> record = buildRecord(
                "statistics.teams.out",
                key(payload.teamId(), payload.year()),
                payload
        );
        kafkaTemplate.send(record);
    }

    public void sendPlayerStats(PlayerStatResponse payload) {
        ProducerRecord<String, Object> record = buildRecord(
                "statistics.players.out",
                key(payload.playerId(), payload.year()),
                payload
        );
        kafkaTemplate.send(record);
    }

    public void sendTopTeams(List<TeamStatResponse> payload, Integer year, Integer limit) {
        String recordKey = "year=" + year + ";limit=" + limit;

        ProducerRecord<String, Object> record = buildRecord(
                "statistics.top-teams.out",
                recordKey,
                payload
        );
        kafkaTemplate.send(record);
    }

    public void sendTopScorers(List<TopScorersStatResponse> payload, Long teamId, Integer year, Integer limit) {
        String recordKey = "teamId=" + teamId + ";year=" + year + ";limit=" + limit;

        ProducerRecord<String, Object> record = buildRecord(
                "statistics.top-scores.out",
                recordKey,
                payload
        );
        kafkaTemplate.send(record);
    }

    private ProducerRecord<String, Object> buildRecord(String topic, String key, Object payload) {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(topic, null, null, key, payload);
        record.headers().add(header("source", "stats-api"));
        record.headers().add(header("version", "1.0"));
        record.headers().add(header("content-type", "application/json"));
        return record;
    }

    private Header header(String name, String value) {
        return new RecordHeader(name, value.getBytes(StandardCharsets.UTF_8));
    }

    private String key(Object id, Integer year) {
        return "id=" + id + ";year=" + year;
    }
}
