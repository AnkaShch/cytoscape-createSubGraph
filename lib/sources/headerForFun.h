#ifndef JNI_CPP_HEADERFORFUN_H
#define JNI_CPP_HEADERFORFUN_H

#include <vector>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <jni.h>

class ListJNI {
public:
    jclass java_util_ArrayList;
    jmethodID java_util_ArrayList_Constructor;
    jmethodID java_util_ArrayList_size;
    jmethodID java_util_ArrayList_get;
    jmethodID java_util_ArrayList_add;
    JNIEnv* env;

    void init(JNIEnv*);
    std::vector<std::string> java2cpp(jobject arrayList);
    jobject cpp2java(const std::vector<std::string>& vector);
};


std::pair<std::string, std::string> splitEdges(const std::string &edge);

void createGraph(std::unordered_map<std::string, std::vector<std::string>> &graph,
                 const std::vector<std::pair<std::string, std::string>> &edgeList);

void dfs(std::unordered_map<std::string, std::vector<std::string>> &graph,
         std::unordered_set<std::string> &subGraphNodes, const std::string &v, int depth, int i);

std::vector<std::string> createSubGraph(std::unordered_map<std::string, std::vector<std::string>> &graph,
                                        const std::string &root);
#endif //JNI_CPP_HEADERFORFUN_H
