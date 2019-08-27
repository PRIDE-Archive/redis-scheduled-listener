package uk.ac.ebi.pride.jobschedule.redisscheduledlistener.message.model;

import lombok.*;

/** @author Suresh Hewapathirana */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AssayDataGenerationPayload {
    @NonNull private String accession;
    @NonNull private String assayAccession;
}
