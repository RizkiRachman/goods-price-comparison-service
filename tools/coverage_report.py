import xml.etree.ElementTree as ET
tree = ET.parse('target/site/jacoco/jacoco.xml')
root = tree.getroot()

out = []
out.append("=== Packages below 90% instruction or 85% branch ===")
out.append(f"{'Package':<60} {'Inst':>6} {'Line':>6} {'Branch':>6} {'Missed'}")
out.append("-" * 85)
items = []
for pkg in root.findall("package"):
    name = pkg.get("name")
    if "/port/" in name: continue
    c = {t.get("type"): (int(t.get("missed")), int(t.get("covered"))) for t in pkg.findall("counter")}
    im, ic = c.get("INSTRUCTION", (0,0))
    bm, bc = c.get("BRANCH", (0,0))
    lm, lc = c.get("LINE", (0,0))
    ti, tb = im+ic, bm+bc
    ip = round(100*ic/ti,1) if ti>0 else 100
    bp = round(100*bc/tb,1) if tb>0 else 100
    lp = round(100*lc/(lm+lc),1) if (lm+lc)>0 else 100
    if ip < 90 or (tb > 0 and bp < 85):
        n = name.replace("com/example/goodsprice/","")
        items.append((ip, n, lp, bp, im))

items.sort()
for ip, n, lp, bp, im in items:
    out.append(f"{n:<60} {ip:>5.1f}% {lp:>5.1f}% {bp:>5.1f}% {im:>3}")

print("\n".join(out))
