package edu.neu.cs6510.sp25.t1.cli.util;

import edu.neu.cs6510.sp25.t1.cli.model.CheckCommandValidationResult;
import java.io.FileInputStream;
import java.nio.file.Path;
import org.yaml.snakeyaml.Yaml
import java.util.Map;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class PipelineValidator {
  public CheckCommandValidationResult checkCommandValidationResult(Path configPath){
    // TODO: replace validation function

    try{
      // Verify YAML file is valid
      Map<String, Object> yaml = parseYamlFile(configPath);
      if (yaml == null){
        return CheckCommandValidationResult.error("Invalid YAML file", 0, 0, configPath);
      }

      // Verify contain required field
      CheckCommandValidationResult requiredFields = validateRequiredFields(yaml, configPath);
      if (!requiredFields.isValid()) {
        return requiredFields;
      }

      // Verify pipeline section
      CheckCommandValidationResult pipelineSection = validatePipelineSection(yaml, configPath);
      if (!pipelineSection.isValid()) {
        return pipelineSection;
      }

      // Verify stage section
      CheckCommandValidationResult stagesSection = validateStagesSection(yaml, configPath);
      if (!stagesSection.isValid()) {
        return stagesSection;
      }

      // Verify job and dependecies(include check cycle)
      return validateJobsAndDependencies(yaml, configPath);

    }catch (MarkedYAMLException e){
      return CheckCommandValidationResult.error(
          e.getProblem(),
          e.getProblemMark().getLine()+1,
          e.getProblemMark().getColumn()+1,
          configPath
      );
    }catch (Exception e){
      return CheckCommandValidationResult.error(e.getMessage(), 0, 0, configPath);
    }
  }

  private Map<String, Object> parseYamlFile(Path configPath) {
    try(FileInputStream fis = new FileInputStream(configPath.toFile())){
      Yaml yaml = new Yaml();
      return yaml.load(fis);
    }catch(Exception e){
      return null;
    }
  }

}
