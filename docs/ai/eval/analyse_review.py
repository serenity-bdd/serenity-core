#!/usr/bin/env python3
"""Mean living-documentation scores (0-2 per check) per results directory: analyse_review.py DIR [DIR ...]"""
import glob, json, os, sys, collections

KEYS = ["A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"]
for d in sys.argv[1:]:
    rows = [json.load(open(f)) for f in glob.glob(os.path.join(d, "review", "*.json"))]
    rows = [r for r in rows if r.get("scores")]
    if not rows:
        print(d, "no reviews"); continue
    mean = {k: sum(r["scores"].get(k, 0) for r in rows) / len(rows) for k in KEYS}
    total = sum(mean.values()) / (2 * len(KEYS))
    print("%-34s n=%-3d overall %.0f%%  " % (os.path.basename(d.rstrip("/")), len(rows), 100 * total) + " ".join("%s %.1f" % (k, mean[k]) for k in KEYS))
