from __future__ import print_function
import sys
import json

print("PYTHON, args=",sys.argv,file=sys.stderr)

with sys.stdin as infile:
    for line in infile:
        print("PYTHON, input=",line,file=sys.stderr)
        if line == "STOP":
            break
        retmap = {}
        retmap["status"] = "ok"
        retmap["output"] = "theClass"
        retmap["additional"] = 22
        print(json.dumps(retmap))
        sys.stdout.flush()
print("PYTHON: finishing",file=sys.stderr)
