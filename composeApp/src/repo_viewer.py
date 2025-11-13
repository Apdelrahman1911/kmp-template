from pathlib import Path
from jinja2 import Template
from pygments import highlight
from pygments.lexers import guess_lexer_for_filename
from pygments.formatters import HtmlFormatter

# ─────────────────────────────
# CONFIG
# ─────────────────────────────
SKIP_EXTS = {
    ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".svg",
    ".mp4", ".mov", ".avi", ".mkv", ".webm",
    ".mp3", ".wav", ".ogg", ".flac",
    ".ico", ".pdf", ".zip", ".rar", ".7z", ".tar", ".gz",
    ".exe", ".dll", ".so", ".bin", ".dat"
}

repo = Path(".")
files = [f for f in repo.rglob("*") if f.is_file() and f.suffix.lower() not in SKIP_EXTS]
formatter = HtmlFormatter(style="monokai", full=False)

# ─────────────────────────────
# BUILD FOLDER TREE
# ─────────────────────────────
def generate_tree(start_path: Path, prefix=""):
    entries = sorted(list(start_path.iterdir()), key=lambda x: (x.is_file(), x.name.lower()))
    lines = []
    for i, entry in enumerate(entries):
        connector = "└── " if i == len(entries) - 1 else "├── "
        if entry.is_dir():
            lines.append(f"{prefix}{connector}{entry.name}")
            new_prefix = prefix + ("    " if i == len(entries) - 1 else "│   ")
            lines.extend(generate_tree(entry, new_prefix))
        elif entry.suffix.lower() not in SKIP_EXTS:
            lines.append(f"{prefix}{connector}{entry.name}")
    return lines

tree_text = "\n".join(generate_tree(repo))

# ─────────────────────────────
# BUILD CODE CONTENTS
# ─────────────────────────────
text_dump = [f"📁 Repository Structure\n{tree_text}\n\n📄 Code Contents\n"]
code_blocks_html = []
for f in sorted(files):
    rel_path = f.relative_to(repo)
    file_id = str(rel_path).replace("/", "_").replace("\\", "_")
    try:
        code = f.read_text(errors="ignore")
    except Exception:
        continue

    # Add to plain text dump
    text_dump.append(f"==== {rel_path} ====\n{code}\n\n")

    # Highlight for HTML
    try:
        lexer = guess_lexer_for_filename(f.name, code)
    except Exception:
        from pygments.lexers import TextLexer
        lexer = TextLexer()
    highlighted = highlight(code, lexer, formatter)
    code_blocks_html.append(f"<h2 id='{file_id}'>{rel_path}</h2>{highlighted}")

# Write plain text file
dump_path = Path("repo_dump.txt")
dump_path.write_text("\n".join(text_dump), encoding="utf-8")

# ─────────────────────────────
# TEMPLATE
# ─────────────────────────────
template = Template("""
<html>
<head>
<meta charset="utf-8">
<title>Repo Viewer</title>
<style>
body {
  background: #272822; color: #f8f8f2;
  font-family: monospace; padding: 20px;
}
pre.tree {
  background: #1e1f1c; padding: 15px;
  border-radius: 6px; color: #a6e22e;
  overflow-x: auto; line-height: 1.4;
}
h1 { color: #a6e22e; }
h2 {
  margin-top: 40px;
  border-bottom: 1px solid #444;
  color: #66d9ef;
  font-size: 1.1rem;
}
button {
  background: #66d9ef; color: #272822;
  border: none; padding: 10px 15px;
  margin-right: 10px; border-radius: 4px;
  cursor: pointer; font-weight: bold;
}
button:hover { background: #5cb6cf; }
#buttons { margin-bottom: 20px; }
{{ formatter.get_style_defs('.highlight') }}
</style>
</head>
<body>
<h1>📁 Repository Structure</h1>

<div id="buttons">
  <button onclick="copyAll()">📋 Copy All Text</button>
  <button onclick="downloadText()">💾 Download as .txt</button>
</div>

<pre id="tree" class="tree">{{ tree_text }}</pre>

<h1>📄 Code Contents</h1>
<div id="code">{{ code_blocks|safe }}</div>

<script>
function copyAll() {
  const text = document.getElementById("tree").innerText + "\\n\\n" + document.getElementById("code").innerText;
  navigator.clipboard.writeText(text)
    .then(() => alert("✅ All text copied to clipboard!"))
    .catch(err => alert("❌ Failed to copy: " + err));
}

function downloadText() {
  const text = document.getElementById("tree").innerText + "\\n\\n" + document.getElementById("code").innerText;
  const blob = new Blob([text], {type: "text/plain"});
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "repo_dump.txt";
  a.click();
  URL.revokeObjectURL(url);
}
</script>
</body>
</html>
""")

# ─────────────────────────────
# RENDER OUTPUT
# ─────────────────────────────
html_output = template.render(
    tree_text=tree_text,
    code_blocks="".join(code_blocks_html),
    formatter=formatter
)
Path("repo_view.html").write_text(html_output, encoding="utf-8")

print("✅ Generated repo_view.html and repo_dump.txt (with copy/download buttons)")
