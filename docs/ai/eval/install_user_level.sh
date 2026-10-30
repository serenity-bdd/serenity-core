#!/bin/sh
# Install or remove the skill and reviewer agent at user level (~/.claude), for the user-level install test.
# usage: install_user_level.sh install|remove      Refuses to overwrite anything that is already there.
set -e
SK=~/.claude/skills/serenity-bdd; AG=~/.claude/agents/serenity-test-reviewer.md
SRC="$(cd "$(dirname "$0")/../skill" && pwd)"
case "$1" in
  install)
    [ ! -e "$SK" ] && [ ! -e "$AG" ] || { echo "already installed or in the way: $SK $AG"; exit 1; }
    mkdir -p ~/.claude/skills ~/.claude/agents
    cp -R "$SRC/serenity-bdd" "$SK"; rm -f "$SK/check-examples.sh"
    cp "$SRC/agents/serenity-test-reviewer.md" "$AG"
    echo "installed $SK and $AG";;
  remove) rm -rf "$SK" "$AG"; echo "removed";;
  *) echo "usage: $0 install|remove"; exit 2;;
esac
