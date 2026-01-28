const fs = require("fs");
const path = require("path");

const ROOT = "./src/main/kotlin"; // Kotlin source root

function toPhaseId(name) {
  const match = name.match(/^([a-z])_phase$/);
  if (!match) return toId(name);
  return `${match[1]}_phase`;
}

function toId(name) {
  return name
    .replace(/\.[^/.]+$/, "")
    .replace(/[^\w]+/g, "_")
    .toLowerCase();
}

function titleize(name) {
  return name
    .replace(/^[a-z]_/, "")
    .replace(/\.[^/.]+$/, "")
    .replace(/_/g, " ")
    .replace(/\b\w/g, c => c.toUpperCase());
}

// Read Kotlin example files inside a subtopic
function walkExamples(subPath) {
  const examplesDir = path.join(subPath, "examples");
  if (!fs.existsSync(examplesDir)) return [];

  return fs.readdirSync(examplesDir)
    .filter(f => !fs.statSync(path.join(examplesDir, f)).isDirectory())
    .map(file => {
      const id = toId(file);
      return {
        id,
        title: titleize(file),
        file,
        description: "",
        key: `topics_list.${id}`,
        contentKey: titleize(file).replace(/\s/g, "") + "Composable" // <-- add this
      };
    });
}

// Read subtopics, include README.md in content folder, keep examples
function walkSubtopics(topicPath, phaseId) {
  return fs.readdirSync(topicPath)
    .filter(d => fs.statSync(path.join(topicPath, d)).isDirectory())
    .map(sub => {
      const subPath = path.join(topicPath, sub);
      const topicFolder = path.basename(topicPath);

      return {
        id: toId(sub),
        title: titleize(sub),
        path: path.join("content", phaseId, topicFolder, sub, "README.md"),
        examples: walkExamples(subPath)
      };
    });
}

// Read topics inside a phase
function walkTopics(phasePath, phaseId) {
  return fs.readdirSync(phasePath)
    .filter(d => fs.statSync(path.join(phasePath, d)).isDirectory())
    .map(topic => ({
      id: toId(topic),
      title: titleize(topic),
      subtopics: walkSubtopics(path.join(phasePath, topic), phaseId)
    }));
}

// Read all phases
function walkPhases(root) {
  return fs.readdirSync(root)
    .filter(d => d.endsWith("_phase"))
    .map((phase, index) => {
      const phaseId = toPhaseId(phase);
      return {
        id: phaseId,
        title: titleize(phase),
        order: index + 1,
        topics: walkTopics(path.join(root, phase), phaseId)
      };
    });
}

const output = {
  domain: "kotlin",
  phases: walkPhases(ROOT)
};

// Write JSON to assets
fs.writeFileSync(
  "./src/main/assets/content/topics_list.json",
  JSON.stringify(output, null, 2)
);

console.log("✅ JSON generated: topics_list.json (README paths + Kotlin examples preserved)");
