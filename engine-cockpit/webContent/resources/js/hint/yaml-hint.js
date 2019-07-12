var yamlHintDirectory = ["test", "tasldkfj"];

// Register our custom Codemirror hint plugin.
CodeMirror.registerHelper('hint', 'yamlHint', function(editor) {
    var cur = editor.getCursor();
    var curLine = editor.getLine(cur.line);
    var start = cur.ch;
    var end = start;
    while (end < curLine.length && /[\w$]/.test(curLine.charAt(end))) ++end;
    while (start && /[\w$]/.test(curLine.charAt(start - 1))) --start;
    var curWord = start !== end && curLine.slice(start, end);
    var regex = new RegExp('^' + curWord, 'i');
    return {
        list: (!curWord ? [] : yamlHintDirectory.filter(function(item) {
            return item.match(regex);
        })).sort(),
        from: CodeMirror.Pos(cur.line, start),
        to: CodeMirror.Pos(cur.line, end)
    }
});

