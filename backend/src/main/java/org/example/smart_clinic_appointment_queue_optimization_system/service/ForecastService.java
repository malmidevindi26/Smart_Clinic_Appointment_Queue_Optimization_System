package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.DoctorForecastDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ForecastReportDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.AppointmentRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    private static final double FLAT_TREND_THRESHOLD = 0.05; // slope smaller than this => "STABLE"

    public List<DoctorForecastDto> forecastNextPeriod(int lookbackDays) {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(lookbackDays);

        List<Object[]> rawRows = appointmentRepository.findDailyAppointmentCountsPerDoctor(fromDate);

        Map<Long, Map<LocalDate, Long>> countsByDoctor = new HashMap<>();
        for (Object[] row : rawRows) {
            Long doctorId = toLong(row[0]);
            LocalDate date = toLocalDate(row[1]);
            Long count = toLong(row[2]);
            if (doctorId == null || date == null) continue; // skip malformed row defensively

            countsByDoctor
                    .computeIfAbsent(doctorId, k -> new HashMap<>())
                    .put(date, count);
        }

        List<Doctor> allDoctors = doctorRepository.findAll();
        List<DoctorForecastDto> results = new ArrayList<>();

        for (Doctor doctor : allDoctors) {
            Map<LocalDate, Long> history = countsByDoctor.getOrDefault(doctor.getId(), Collections.emptyMap());

            double[] xValues = new double[lookbackDays];
            double[] yValues = new double[lookbackDays];
            double sumY = 0;

            for (int i = 0; i < lookbackDays; i++) {
                LocalDate day = fromDate.plusDays(i);
                long count = history.getOrDefault(day, 0L);
                xValues[i] = i;
                yValues[i] = count;
                sumY += count;
            }

            double historicalAverage = lookbackDays == 0 ? 0 : sumY / lookbackDays;

            double[] mc = simpleLinearRegression(xValues, yValues);
            double slope = mc[0];
            double intercept = mc[1];

            double predictedRaw = slope * lookbackDays + intercept;
            double predicted = Math.max(0, round1(predictedRaw));

            String trend = slope > FLAT_TREND_THRESHOLD ? "INCREASING"
                    : slope < -FLAT_TREND_THRESHOLD ? "DECREASING"
                    : "STABLE";

            double trendPercentage = historicalAverage == 0
                    ? 0
                    : round1(((predicted - historicalAverage) / historicalAverage) * 100.0);

            results.add(new DoctorForecastDto(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    round1(historicalAverage),
                    predicted,
                    trend,
                    trendPercentage
            ));
        }

        results.sort((a, b) -> Double.compare(b.getPredictedAppointments(), a.getPredictedAppointments()));
        return results;
    }

    public ForecastReportDto generateReport(int lookbackDays) {
        List<DoctorForecastDto> forecasts = forecastNextPeriod(lookbackDays);

        String busiestName = "N/A";
        double busiestCount = 0;
        if (!forecasts.isEmpty()) {
            DoctorForecastDto top = forecasts.get(0);
            busiestName = top.getDoctorName();
            busiestCount = top.getPredictedAppointments();
        }

        String summary = buildSummaryText(forecasts, lookbackDays, busiestName, busiestCount);

        return new ForecastReportDto(
                LocalDateTime.now(),
                lookbackDays,
                forecasts.size(),
                busiestName,
                busiestCount,
                summary,
                forecasts
        );
    }

    private double[] simpleLinearRegression(double[] x, double[] y) {
        int n = x.length;
        if (n < 2) {
            double meanY = n == 0 ? 0 : y[0];
            return new double[]{0, meanY};
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double denominator = (n * sumX2) - (sumX * sumX);
        if (denominator == 0) {
            return new double[]{0, sumY / n};
        }

        double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
        double intercept = (sumY - slope * sumX) / n;
        return new double[]{slope, intercept};
    }

    private String buildSummaryText(List<DoctorForecastDto> forecasts, int lookbackDays,
                                    String busiestName, double busiestCount) {
        if (forecasts.isEmpty()) {
            return "No appointment history was found for the selected period ("
                    + lookbackDays + " days), so no forecast could be generated.";
        }

        long increasing = forecasts.stream().filter(f -> f.getTrend().equals("INCREASING")).count();
        long decreasing = forecasts.stream().filter(f -> f.getTrend().equals("DECREASING")).count();
        long stable = forecasts.size() - increasing - decreasing;

        String busiestCountText = String.format("%.1f", busiestCount);

        return "Based on the last " + lookbackDays + " days of appointment records, " + busiestName +
                " is projected to have the highest patient demand next, with an estimated " +
                busiestCountText + " appointments in the upcoming period. Across all " + forecasts.size() +
                " doctors analyzed: " + increasing + " showing an increasing booking trend, " + stable +
                " stable, and " + decreasing + " decreasing. This forecast uses a linear regression " +
                "trend line fitted to each doctor's historical daily booking counts. ";
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate ld) return ld;
        if (value instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        if (value instanceof java.sql.Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        if (value instanceof java.time.LocalDateTime ldt) return ldt.toLocalDate();
        return null;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        return null;
    }
}