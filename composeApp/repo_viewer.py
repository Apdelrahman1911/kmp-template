from pathlib import Path
from jinja2 import Template
from pygments import highlight
from pygments.lexers import guess_lexer_for_filename
from pygments.formatters import HtmlFormatter

repo = Path(".")  # your project root
files = [f for f in repo.rglob("*") if f.is_file()]

formatter = HtmlFormatter(style="monokai", full=False)
html_blocks = []

for f in files:
    code = f.read_text(errors="ignore")
    try:
        lexer = guess_lexer_for_filename(f.name, code)
    except:
        from pygments.lexers import TextLexer
        lexer = TextLexer()
    highlighted = highlight(code, lexer, formatter)
    html_blocks.append(f"<h2>{f}</h2><pre>{highlighted}</pre>")

template = f"""
<html>
<head>
<style>{formatter.get_style_defs('.highlight')}</style>
<title>Local Repo Viewer</title>
</head>
<body>
{"".join(html_blocks)}
</body>
</html>
"""

Path("repo_view.html").write_text(template)
print("✅ Generated repo_view.html")
