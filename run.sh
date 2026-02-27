#!/bin/bash
# =============================================
# Clothify Store POS - Run Script
# Usage: ./run.sh
# =============================================

export JAVA_HOME="/c/Users/DELL/.jdks/openjdk-23.0.1"
export PATH="$JAVA_HOME/bin:$PATH"
MVN="C:/Program Files/JetBrains/IntelliJ IDEA 2025.3.2/plugins/maven/lib/maven3/bin/mvn.cmd"

echo "========================================"
echo "  Clothify Store POS - Starting..."
echo "========================================"
echo ""
echo "Make sure MySQL is running first!"
echo ""

"$MVN" javafx:run
