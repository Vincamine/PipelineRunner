package edu.neu.cs6510.sp25.t1.cli.model;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckCommandValidationResult {
  private final boolean isValid;
  private final String errorMessage;
  private final int line;
  private final int column;
  private final Path filePath;

  public CheckCommandValidationResult(boolean isValid, String errorMessage, int line, int column, Path filePath){
    this.isValid = isValid;
    this.errorMessage = errorMessage;
    this.line = line;
    this.column = column;
    this.filePath = filePath;
  }

  public static CheckCommandValidationResult success(Path filePath){
    return new CheckCommandValidationResult(true, null, 0, 0, filePath);
  }

  public static CheckCommandValidationResult error(String message, int line, int column, Path filePath){
    return new CheckCommandValidationResult(false, message, line, column, filePath);
  }

  public boolean isValid(){return isValid;}
  public String getErrorMessage(){return errorMessage;}
  public int getLine(){return line;}
  public int getColumn(){return column;}
  public Path getFilePath(){return filePath;}

  public String toIDEString(){
    if (isValid){
      return "Configuration is valid";
    }
    return String.format("%s%d%d%s",
        filePath.toAbsolutePath(),
        line,
        column,
        errorMessage);
  }

  public String toDetailedString(){
    if (isValid){
      return "Configuration is valid";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(toIDEString()).append("\n\n");

    try{
      String content = new String(Files.readAllBytes(filePath));
      String[] lines = content.split("\n");
      if (line > 0 && line <= lines.length) {
        int contextLines = 2;
        int startLine = Math.max(1, line - contextLines);
        int endLine = Math.min(lines.length, line + contextLines);

        for (int i = startLine; i <= endLine; i++) {
          String prefix = (i == line) ? ">" : " ";
          sb.append(String.format("%s %4d | %s%n", prefix, i, lines[i - 1]));

          if (i == line) {
            sb.append("  ")
                .append("     | ")
                .append(" ".repeat(column - 1))
                .append("^")
                .append("\n");
          }
        }
      }
    }catch(Exception e){
      sb.append("Unable to read file content for detailed error message");
    }
    return sb.toString();

  }

}
