import Editor from "@monaco-editor/react";

function CodeEditor({ language, code, onChange }) {
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

  return (
    <div className="code-editor-wrapper">
      <Editor
        height="100%"
        language={getMonacoLanguage()}
        value={code}
        theme="vs-dark"
        onChange={(value) => onChange(value || "")}
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
        }}
      />
    </div>
  );
}

export default CodeEditor;
