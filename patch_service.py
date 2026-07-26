import sys

path = sys.argv[1]
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

with open("my-files/RHVoiceService_QOSHIMCHA_KOD.java", "r", encoding="utf-8") as f:
    snippet = f.read()

marker = "String profileSpec = voiceProfileSpecBuilder.toString();"
if marker not in content:
    print("MARKER NOT FOUND")
    sys.exit(1)

content = content.replace(marker, snippet + "\n        " + marker, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(content)

print("patched successfully")
