var yamlHintDirectory = ["test", "tasldkfj"];

CodeMirror.commands.autocomplete = function(cm) {
    CodeMirror.showHint(cm, CodeMirror.hint.yamlHint);
};

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
    
CodeMirror.defineOption('showInvisibles', false, (cm, val, prev) => {
    let Count = 0;
    const Maximum = cm.getOption('maxInvisibles') || 16;
    
    if (prev === CodeMirror.Init)
        prev = false;
    
    if (prev && !val) {
        cm.removeOverlay('invisibles');
        return rm();
    }
    
    if (!prev && val) {
        add(Maximum);
     
        cm.addOverlay({
            name: 'invisibles',
            token: function nextToken(stream) {
                let spaces = 0;
                let peek = stream.peek() === ' ';
                
                if (peek) {
                    while (peek && spaces < Maximum) {
                        ++spaces;
                        
                        stream.next();
                        peek = stream.peek() === ' ';
                    }
                    
                    let ret = 'whitespace whitespace-' + spaces;
                    
                    /*
                     * styles should be different
                     * could not be two same styles
                     * beside because of this check in runmode
                     * function in `codemirror.js`:
                     *
                     * 6624: if (!flattenSpans || curStyle != style) {}
                     */
                    if (spaces === Maximum)
                        ret += ' whitespace-rand-' + Count++;
                    
                    return ret;
                }
                
                while (!stream.eol() && !peek) {
                    stream.next();
                    
                    peek = stream.peek() === ' ';
                }
                
                return 'cm-eol';
            }
        });
    }
});

function add(max) {
    const classBase = '.CodeMirror .cm-whitespace-';
    const spaceChar = '·';
    const style = document.createElement('style');
    
    style.setAttribute('data-name', 'js-show-invisibles');
    
    let rules = '';
    let spaceChars = '';
    
    for (let i = 1; i <= max; ++i) {
        spaceChars += spaceChar;
        
        const rule = classBase + i + '::before { content: "' + spaceChars + '";}\n';
        rules += rule;
    }
    
    style.textContent = getStyle() + '\n' + getEOL() + '\n' + rules;
    
    document.head.appendChild(style);
}

function rm() {
    const style = document.querySelector('[data-name="js-show-invisibles"]');
    
    document.head.removeChild(style);
}

function getStyle() {
    const style = [
        '.cm-whitespace::before {',
        '    position: absolute;',
        '    pointer-events: none;',
        '    color: darkgray;',
        '}'
    ].join('');
    
    return style;
}

function getEOL() {
    const style = [
        '.CodeMirror-code > div > pre > span::after, .CodeMirror-line > span::after {',
        '    pointer-events: none;',
        '    color: #404F7D;',
        '    content: "¬"',
        '    display: none',
        '}',
    ].join('');
    
    return style;
}