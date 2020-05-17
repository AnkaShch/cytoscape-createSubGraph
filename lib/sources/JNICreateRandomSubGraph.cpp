#include <fstream>
#include "JNICreateRandomSubGraph.h"
#include "headerForFun.h"


void ListJNI::init(JNIEnv* envx) {
    env = envx;
    java_util_ArrayList      = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
    java_util_ArrayList_Constructor = env->GetMethodID(java_util_ArrayList, "<init>", "()V");
    java_util_ArrayList_size = env->GetMethodID (java_util_ArrayList, "size", "()I");
    java_util_ArrayList_get  = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
    java_util_ArrayList_add  = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");
}

// ==================== Java ArrayList<String> ==> C++ vector<string> ====================
std::vector<std::string> ListJNI::java2cpp(jobject arrayList) {
    jint len = env->CallIntMethod(arrayList, java_util_ArrayList_size);
    std::vector<std::string> result;
    result.reserve(len);
    for (jint i=0; i<len; i++) {
        jstring element = static_cast<jstring>(env->CallObjectMethod(arrayList, java_util_ArrayList_get, i));
        const char* pchars = env->GetStringUTFChars(element, nullptr);
        result.emplace_back(pchars);
        env->ReleaseStringUTFChars(element, pchars);
        env->DeleteLocalRef(element);
    }
    return result;
}
// ==================== C++ vector<string> ==> Java ArrayList<String> ====================
jobject ListJNI::cpp2java(const std::vector<std::string>& vector) {
        jobject result = env->NewObject(java_util_ArrayList, java_util_ArrayList_Constructor, vector.size());
        for (std::string s: vector) {
            jstring element = env->NewStringUTF(s.c_str());
            env->CallVoidMethod(result, java_util_ArrayList_add, element);
            env->DeleteLocalRef(element);
        }
        return result;
}

std::pair<std::string, std::string> splitEdges(const std::string &edge) {
    int pos = edge.find(' ');
    std::string left, right;
    left = edge.substr(0, pos);
    right = edge.substr(pos + 1, edge.size());
    return {left, right};
}

void createGraph(std::unordered_map<std::string, std::vector<std::string>> &graph,
                 const std::vector<std::pair<std::string, std::string>> &edgeList) {
    for (auto edge : edgeList) {
        graph[edge.first].push_back(edge.second);
        graph[edge.second].push_back(edge.first);
    }
}

void dfs(std::unordered_map<std::string, std::vector<std::string>> &graph,
         std::unordered_set<std::string> &subGraphNodes, const std::string &v, int depth, int i) {
    if (i == depth)
        return;
    for (const auto &u : graph[v]) {
        if (subGraphNodes.find(u) == subGraphNodes.end()) {
            subGraphNodes.insert(u);
            dfs(graph, subGraphNodes, u, depth, i + 1);
        }
    }
}

std::vector<std::string> createSubGraph(std::unordered_map<std::string, std::vector<std::string>> &graph,
                                        const std::string &root) {
    std::unordered_set<std::string> subGraphNodes;
    subGraphNodes.insert(root);
    int depth = rand() % 9 + 1;
    int i = 1;
    dfs(graph, subGraphNodes, root, depth, i);
    std::vector<std::string> result;
    result.reserve(subGraphNodes.size());
    for (auto v : subGraphNodes) {
        result.push_back(v);
    }
    return result;
}

JNIEXPORT jobject JNICALL Java_anka_myapp_internal_JNICreateRandomSubGraph_getSubGraphNodes
        (JNIEnv * env, jclass, jobject javaNodeList, jobject javaEdgeList) {
    ListJNI jni;
    jni.init(env);
    std::vector<std::string> nodeList = jni.java2cpp(javaNodeList);
    std::vector<std::string> list = jni.java2cpp(javaEdgeList);
    std::vector<std::pair<std::string, std::string>> edgeList;
    for (auto edge : list) {
        edgeList.push_back(splitEdges(edge));
    }
    std::unordered_map<std::string, std::vector<std::string>> graph;
    createGraph(graph, edgeList);
    srand(time(0));
    std::string root = nodeList[rand() % nodeList.size()];
    std::vector<std::string> subGraph = createSubGraph(graph, root);
    return jni.cpp2java(subGraph);
}


