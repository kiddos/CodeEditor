package app.kiddos.codeeditor.data

object SampleCodes {
    val cppSample = """
// Modern C++20 Demonstration
#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <memory>

template <typename T>
class Stack {
private:
    std::vector<T> elements;

public:
    void push(const T& item) {
        elements.push_back(item);
    }

    T pop() {
        if (elements.empty()) {
            throw std::runtime_error("Stack is empty!");
        }
        T top = elements.back();
        elements.pop_back();
        return top;
    }

    bool empty() const {
        return elements.empty();
    }
};

int main() {
    std::cout << "Hello from C++20!" << std::endl;
    
    Stack<std::string> stack;
    stack.push("Rust");
    stack.push("C++");
    stack.push("Python");

    std::vector<int> numbers = {10, 42, 5, 88, 3};
    std::sort(numbers.begin(), numbers.end(), [](int a, int b) {
        return a < b;
    });

    for (const auto& num : numbers) {
        std::cout << "Value: " << num << "\n";
    }

    return 0;
}
""".trimIndent()

    val pythonSample = """
# Python 3.12 Demonstration
import asyncio
from dataclasses import dataclass
from typing import List, Optional

@dataclass
class Developer:
    name: str
    languages: List[str]
    years_experience: int
    is_active: bool = True

    def summary(self) -> str:
        langs = ", ".join(self.languages)
        return f"{self.name} has {self.years_experience} years of exp with: {langs}"

async def fetch_code_metrics(dev: Developer) -> dict:
    await asyncio.sleep(0.1)  # Simulate async work
    total_lines = len(dev.languages) * 1500
    return {
        "dev": dev.name,
        "estimated_lines": total_lines,
        "rating": 9.8
    }

def main():
    team = [
        Developer("Alice", ["Rust", "Python"], 6),
        Developer("Bob", ["C++", "Java", "Go"], 8),
        Developer("Charlie", ["Kotlin", "TypeScript"], 4),
    ]

    print("--- Developer Profiles ---")
    for member in team:
        print(member.summary())

    active_vets = [d.name for d in team if d.years_experience >= 5]
    print(f"Senior developers: {active_vets}")

if __name__ == "__main__":
    main()
""".trimIndent()

    val rustSample = """
// Modern Rust Demonstration
use std::collections::HashMap;

#[derive(Debug, Clone, PartialEq)]
pub enum Status {
    Pending,
    InProgress { task_id: u32 },
    Completed(String),
}

pub struct TaskManager {
    tasks: HashMap<u32, Status>,
}

impl TaskManager {
    pub fn new() -> Self {
        Self {
            tasks: HashMap::new(),
        }
    }

    pub fn add_task(&mut self, id: u32, status: Status) {
        self.tasks.insert(id, status);
    }

    pub fn inspect(&self) {
        for (id, status) in &self.tasks {
            match status {
                Status::Pending => println!("Task #{id} is currently pending."),
                Status::InProgress { task_id } => {
                    println!("Task #{id} running subtask {task_id}");
                }
                Status::Completed(msg) => {
                    println!("Task #{id} finished: \"{msg}\"");
                }
            }
        }
    }
}

fn main() {
    println!("Welcome to the Rust Code Editor!");
    let mut manager = TaskManager::new();
    manager.add_task(1, Status::Pending);
    manager.add_task(2, Status::InProgress { task_id: 101 });
    manager.add_task(3, Status::Completed(String::from("Code Editor is ready!")));

    manager.inspect();
}
""".trimIndent()

    val javaSample = """
package com.example.editor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeProcessor {

    public record CodeSnippet(String id, String language, String code) {}

    public static void main(String[] args) {
        System.out.println("Java 21 Modern Syntax Demonstration");

        List<CodeSnippet> snippets = List.of(
            new CodeSnippet("1", "C++", "int x = 42;"),
            new CodeSnippet("2", "Python", "x = 42"),
            new CodeSnippet("3", "Rust", "let x = 42;"),
            new CodeSnippet("4", "Java", "var x = 42;")
        );

        Map<String, String> langMap = snippets.stream()
            .collect(Collectors.toMap(
                CodeSnippet::language,
                CodeSnippet::code
            ));

        langMap.forEach((lang, code) -> {
            System.out.printf("[%s] => %s%n", lang, code);
        });
    }
}
""".trimIndent()
}
