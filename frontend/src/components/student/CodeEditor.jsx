import Editor from "@monaco-editor/react";

function CodeEditor({
  language,
  code,
  onChange,
  onSecurityViolation,
  readOnly = false,
}) {
  const getMonacoLanguage = () => {
    switch (language) {
      case "CPP":
        return "cpp";

      case "JAVA":
        return "java";

      case "PYTHON":
        return "python";

      default:
        return "cpp";
    }
  };

  const handleEditorChange = (value, changeEvent) => {
    /*
     * Detect a large multi-line insertion.
     *
     * 1-3 inserted lines  -> allowed
     * 4+ inserted lines   -> security violation
     */
    if (changeEvent?.changes?.length === 1) {
      const change = changeEvent.changes[0];
      const insertedText = change.text || "";

      const insertedLineCount =
        insertedText.split(/\r\n|\r|\n/).length;

      if (insertedLineCount > 3 && onSecurityViolation) {
        onSecurityViolation();
      }
    }

    onChange(value || "");
  };

  return (
    <div className="code-editor-wrapper">
      <Editor
        height="100%"
        language={getMonacoLanguage()}
        value={code}
        theme="vs-dark"
        onChange={handleEditorChange}
        options={{
          fontSize: 15,

          minimap: {
            enabled: false,
          },

          automaticLayout: true,

          scrollBeyondLastLine: false,

          wordWrap: "on",

          tabSize: 4,

          insertSpaces: true,

          lineNumbers: "on",

          folding: true,

          bracketPairColorization: {
            enabled: true,
          },

          suggestOnTriggerCharacters: true,

          quickSuggestions: true,

          padding: {
            top: 16,
          },

          readOnly: readOnly,
        }}
      />
    </div>
  );
}

export default CodeEditor;