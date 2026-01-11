package ro.unitbv.smarthome.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InfluxService {

  private final InfluxDBClient client;
  private final String bucket;
  private final String org;

  public record Reading(Instant time, double value) {}

  public InfluxService(InfluxDBClient client, @Value("${influx.bucket}") String bucket, @Value("${influx.org}") String org) {
    this.client = client;
    this.bucket = bucket;
    this.org = org;
  }

  public void writeReading(UUID sensorId, double value) {
    WriteApiBlocking writeApi = client.getWriteApiBlocking();
    Point p = Point.measurement("reading")
        .addTag("sensorId", sensorId.toString())
        .addField("value", value)
        .time(Instant.now(), WritePrecision.NS);
    writeApi.writePoint(bucket, org, p);
  }

  public List<Reading> getReadings(UUID sensorId, String range, int limit) {
    String flux = String.format(
        "from(bucket: \"%s\")\n" +
        "  |> range(start: %s)\n" +
        "  |> filter(fn: (r) => r._measurement == \"reading\" and r.sensorId == \"%s\" and r._field == \"value\")\n" +
        "  |> sort(columns: [\"_time\"], desc: true)\n" +
        "  |> limit(n: %d)",
        bucket, range, sensorId, limit
    );

    List<Reading> out = new ArrayList<>();
    List<FluxTable> tables = client.getQueryApi().query(flux);
    for (FluxTable t : tables) {
      for (FluxRecord r : t.getRecords()) {
        Instant time = r.getTime();
        Object v = r.getValue();
        if (time != null && v != null) {
          out.add(new Reading(time, ((Number) v).doubleValue()));
        }
      }
    }
    return out;
  }

  public Reading latest(UUID sensorId) {
    List<Reading> list = getReadings(sensorId, "-30d", 1);
    return list.isEmpty() ? null : list.get(0);
  }
}
