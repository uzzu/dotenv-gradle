#
# GitHub Comment
#
github.dismiss_out_of_range_messages({
  error: false,
  warning: true,
  message: true,
  markdown: true
})

#
# File watching
#

[
  ".idea/codeStyleSettings.xml",
].each do |file|
  warn("Are you sure want to modify #{file} ?") if git.modified_files.include?(file)
end

#
# Compiler warnings, errors
#

warning_pattern = /w: (?<path>(?:\/.+)+\.kt): \((?<line>\d+), (?<column>\d+)\): (?<description>.*)/
error_pattern = /e: (?<path>(?:\/.+)+\.kt): \((?<line>\d+), (?<column>\d+)\): (?<description>.*)/

target_files = (git.modified_files - git.deleted_files) + git.added_files
kotlin_compile_files = Dir.glob("**/build/kotlin/compile*Kotlin*.stdout")
unless kotlin_compile_files.empty?
  compile_messages = File.read(kotlin_compile_files.first).strip
    .split("\n")
    .each { |s|
      if match = s.match(warning_pattern)
        file = Pathname(match[:path]).relative_path_from(Pathname(Dir.pwd)).to_s
        if git.diff_for_file(file)
          warn("#{match[:description]}", file: file, line: match[:line].to_i)
        else
          warn("#{file}: (#{match[:line]}, #{match[:column]}): #{match[:description]}")
        end
      end
      if match = s.match(error_pattern)
        file = Pathname(match[:path]).relative_path_from(Pathname(Dir.pwd)).to_s
        if git.diff_for_file(file)
          fail("#{match[:description]}", file: file, line: match[:line].to_i)
        else
          fail("#{file}: (#{match[:line]}, #{match[:column]}): #{match[:description]}")
        end
      end
    }
end

#
# ktlint
#
checkstyle_format.base_path = Dir.pwd
Dir.glob('**/build/reports/ktlint/ktlint*Check.xml') do |file|
  checkstyle_format.report file
end

#
# JUnit test results
#

tests = []
failures = []
errors = []
skipped = []

Dir.glob("**/build/test-results/**/TEST-*.xml") do |file|
  junit.parse file
  tests.concat(junit.tests)
  failures.concat(junit.failures)
  errors.concat(junit.errors)
  skipped.concat(junit.skipped)
end
junit.tests = tests
junit.failures = failures
junit.errors = errors
junit.skipped = skipped

junit.report
