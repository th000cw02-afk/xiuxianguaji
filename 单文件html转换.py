import os

assets_dir = r"c:\Users\zhuyue\Desktop\xiuxianguaji\app\src\main\assets"
output_file = os.path.join(assets_dir, "game_combined.html")

with open(os.path.join(assets_dir, "index.html"), "r", encoding="utf-8") as f:
    html_content = f.read()

with open(os.path.join(assets_dir, "styles.css"), "r", encoding="utf-8") as f:
    css_content = f.read()

with open(os.path.join(assets_dir, "math.js"), "r", encoding="utf-8") as f:
    math_content = f.read()

with open(os.path.join(assets_dir, "game.js"), "r", encoding="utf-8") as f:
    game_content = f.read()

with open(os.path.join(assets_dir, "游戏说明.txt"), "r", encoding="utf-8") as f:
    game_guide_content = f.read().replace('\\', '\\\\').replace('`', '\\`').replace('$', '\\$')

html_content = html_content.replace('<link rel="stylesheet" href="styles.css">', f'<style>\n{css_content}\n</style>')

math_config_start = html_content.find('<script src="math.js"></script>')
math_config_end = math_config_start + len('<script src="math.js"></script>')
html_content = html_content[:math_config_start] + f'<script>\n{math_content}\n</script>' + html_content[math_config_end:]

game_script_start = html_content.find('<script src="game.js"></script>')
game_script_end = game_script_start + len('<script src="game.js"></script>')
html_content = html_content[:game_script_start] + f'<script>\n{game_content}\n</script>' + html_content[game_script_end:]

override_script = f'''
<script>
window._embeddedGameGuide = `{game_guide_content}`;
const _originalShowGameGuide = showGameGuide;
showGameGuide = function() {{
    if (window._embeddedGameGuide) {{
        displayGameGuide(window._embeddedGameGuide);
        return;
    }}
    _originalShowGameGuide();
}};
</script>
'''

html_content = html_content.replace('</body>', override_script + '</body>')

with open(output_file, "w", encoding="utf-8") as f:
    f.write(html_content)

print(f"Combined HTML saved to: {output_file}")