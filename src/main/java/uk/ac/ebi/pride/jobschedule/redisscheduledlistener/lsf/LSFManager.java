package uk.ac.ebi.pride.jobschedule.redisscheduledlistener.lsf;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
public class LSFManager {

    /**
     * Get number of running/pending jobs in the LSF queue
     * @param jobName job name
     * @return number of jobs running for the pst_prd user
     */
    public static long getNumberOfJobs(String jobName){

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();

            // Run a shell command
            processBuilder.command("bash", "-c", "bjobs -J " + jobName + "* | wc -l");

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal != 0) {
               log.info("Error occured!");
            }
            return Long.parseLong(output.toString().trim());
        } catch (IOException e) {
            log.error("Exception encountered", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1000000; // large number to stop process
    }
}
