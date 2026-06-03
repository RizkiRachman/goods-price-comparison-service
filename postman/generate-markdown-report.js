const fs = require('fs');
const path = require('path');

const reportPath = path.join(__dirname, 'smoke-test-report.json');
const outputPath = path.join(__dirname, 'SMOKE_TEST_REPORT.md');

if (!fs.existsSync(reportPath)) {
    console.error("Error: smoke-test-report.json not found!");
    process.exit(1);
}

const data = JSON.parse(fs.readFileSync(reportPath, 'utf8'));
const executions = data.run.executions;

let md = `# Smoke Test Execution Report\n\n`;
md += `**Date**: ${new Date().toUTCString()}\n`;
md += `**Total Requests**: ${data.run.stats.requests.total}\n`;
md += `**Passed Assertions**: ${data.run.stats.assertions.pending === 0 && data.run.stats.assertions.failed === 0 ? "All Passed (100%)" : "Some Failed"}\n\n`;

md += `## Execution Summary\n\n`;
md += `| Request Name | Method | URL | Status | Response Time |\n`;
md += `| :--- | :---: | :--- | :---: | :---: |\n`;

executions.forEach(exec => {
    const name = exec.item.name;
    const method = exec.request.method;
    const url = exec.request.url.protocol + "://" + exec.request.url.host.join('.') + "/" + exec.request.url.path.join('/');
    const status = exec.response.code + " " + exec.response.status;
    const time = exec.response.responseTime + "ms";
    md += `| ${name} | **${method}** | \`${url}\` | \`${status}\` | ${time} |\n`;
});

md += `\n---\n\n## Detailed Request & Response Logs\n\n`;

executions.forEach((exec, index) => {
    const name = exec.item.name;
    const method = exec.request.method;
    const url = exec.request.url.protocol + "://" + exec.request.url.host.join('.') + "/" + exec.request.url.path.join('/');
    const status = exec.response.code + " " + exec.response.status;
    const time = exec.response.responseTime + "ms";
    
    md += `### ${index + 1}. ${name}\n\n`;
    md += `- **Endpoint**: \`${method} ${url}\`\n`;
    md += `- **Status**: \`${status}\`\n`;
    md += `- **Response Time**: ${time}\n\n`;
    
    // Request Body
    if (exec.request.body && exec.request.body.raw) {
        md += `<details>\n<summary><b>Request Body</b></summary>\n\n\`\`\`json\n${exec.request.body.raw}\n\`\`\`\n\n</details>\n\n`;
    } else {
        md += `*No Request Body*\n\n`;
    }
    
    // Response Body
    if (exec.response.stream) {
        let resBody = "";
        try {
            const buf = Buffer.from(exec.response.stream);
            resBody = JSON.stringify(JSON.parse(buf.toString()), null, 2);
        } catch (e) {
            resBody = Buffer.from(exec.response.stream).toString();
        }
        md += `<details>\n<summary><b>Response Body</b></summary>\n\n\`\`\`json\n${resBody}\n\`\`\`\n\n</details>\n\n`;
    } else {
        md += `*No Response Body*\n\n`;
    }
    
    md += `---\n\n`;
});

fs.writeFileSync(outputPath, md);
console.log("Markdown report generated successfully at postman/SMOKE_TEST_REPORT.md!");
