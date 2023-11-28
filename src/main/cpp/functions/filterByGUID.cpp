#include "include/functions/filterByGUID.hpp"

void OpenBIMRLEngine::functions::exec(const char *guid)
{
    auto file = OpenBIMRLEngine::getCurrentFile();

    aggregate_of_instance::ptr ptr = file->instances_by_type(std::string(guid));

    for (auto it = ptr->begin(); it != ptr->end(); it++)
    {
        std::cout << *it << std::endl;
    }
}