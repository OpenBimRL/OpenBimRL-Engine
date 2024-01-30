#include <ifcparse/IfcBaseClass.h>
#include <ifcparse/Ifc4.h>
#include <ifcparse/Ifc2x3.h>

#include <boost/optional.hpp>

#include "utils/ifc_properties.hpp"
#include "utils/OpenBIMRL/ifcStandards.hpp"

typedef union
{
    Ifc4::IfcPropertySet *ifc4PropertySet;
    Ifc2x3::IfcPropertySet *ifc2x3PropertySet;
} ifc_property_set;

static std::vector<ifc_property_set> properties;

std::size_t initPropertyIterator(JNA::Pointer ifcPointer)
{
    properties.clear();

    const auto currentPointer = static_cast<IfcUtil::IfcBaseClass *>(ifcPointer);

    if (OpenBIMRLEngine::isIFC4())
    {
        const auto instanceData = currentPointer->as<Ifc4::IfcObject>();
        const auto definitions = instanceData->IsDefinedBy();
        for (auto definition : *definitions)
        {
            try
            {
                auto propertySet = definition->RelatingPropertyDefinition()->as<Ifc4::IfcPropertySet>(true);
                properties.push_back({.ifc4PropertySet = propertySet});
            }
            catch (const IfcParse::IfcException &)
            {
                continue; // ignore this element
            }
        }
    }
    else if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }

    // after filling the list
    return properties.size();
}

std::size_t getBufferSizePropertySetName(std::size_t index)
{
    // failsafe index check
    if (index >= properties.size())
        return 0;

    if (OpenBIMRLEngine::isIFC4())
    {
        const auto name = get_optional_value_or(properties.at(index).ifc4PropertySet->Name(), "");
        return name.length();
    }
    if (OpenBIMRLEngine::isIFC2x3())
    {
        const auto name = get_optional_value_or(properties.at(index).ifc2x3PropertySet->Name(), "");
        return name.length();
    }

    return 0;
}

std::size_t getBufferSizePropertyName(std::size_t propertySetIndex, std::size_t propertyIndex)
{
    // failsafe index check
    if (propertySetIndex >= properties.size())
        return 0;
    if (OpenBIMRLEngine::isIFC4())
    {
        const auto props = properties.at(propertySetIndex).ifc4PropertySet->HasProperties();
        if (propertyIndex >= props->size())
            return 0;
        const auto prop = *(props->begin() + propertyIndex);
        return prop->Name().length();
    }
    if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }

    return 0;
}

std::size_t getNoOfPropertiesInSet(std::size_t index)
{
    // failsafe index check
    if (index >= properties.size())
        return 0;
    if (OpenBIMRLEngine::isIFC4())
        return properties.at(index).ifc4PropertySet->HasProperties()->size();
    if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }
    return 0;
}

bool getPropertySetName(std::size_t index, JNA::Pointer buffer)
{
    if (index >= properties.size())
        return false;
    if (OpenBIMRLEngine::isIFC4())
    {
        const auto name = get_optional_value_or(properties.at(index).ifc4PropertySet->Name(), "");
        memcpy(buffer, name.c_str(), name.size());
        return true;
    }
    if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }
    return false;
}
bool getPropertyName(std::size_t propertySetIndex, std::size_t propertyIndex, JNA::Pointer buffer)
{
    // failsafe index check
    if (propertySetIndex >= properties.size())
        return false;
    if (OpenBIMRLEngine::isIFC4())
    {
        const auto props = properties.at(propertySetIndex).ifc4PropertySet->HasProperties();
        if (propertyIndex >= props->size())
            return false;
        const auto propName = (*(props->begin() + propertyIndex))->Name();

        memcpy(buffer, propName.c_str(), propName.size());

        return true;
    }
    if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }

    return false;
}

static Ifc4::IfcValue *findValue(const Ifc4::IfcProperty *property);

bool getPropertyValue(std::size_t propertySetIndex, std::size_t propertyIndex, JNA::Pointer buffer)
{
    // failsafe index check
    if (propertySetIndex >= properties.size())
        return false;
    if (OpenBIMRLEngine::isIFC4())
    {
        const auto props = properties.at(propertySetIndex).ifc4PropertySet->HasProperties();
        if (propertyIndex >= props->size())
            return false;

        Ifc4::IfcValue *value = findValue(*(props->begin() + propertyIndex));

        if (value == nullptr)
            return false;

        std::stringstream ss;

        // this is horrible
        try
        {
            ss << value->as<Ifc4::IfcLabel>(true)->operator std::string();
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        try
        {
            ss << value->as<Ifc4::IfcIdentifier>(true)->operator std::string();
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        try
        {
            ss << value->as<Ifc4::IfcText>(true)->operator std::string();
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        try
        {
            ss << (value->as<Ifc4::IfcBoolean>(true)->operator bool() ? "true" : "false");
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        try
        {
            ss << value->as<Ifc4::IfcInteger>(true)->operator int();
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        try
        {
            ss << value->as<Ifc4::IfcReal>(true)->operator double();
        }
        catch (const IfcParse::IfcException &)
        { // ignore me
        }
        const auto str = ss.str();

        if (str.empty()) return false;

        memcpy(buffer, str.c_str(), str.length());

        return true;
    }
    if (OpenBIMRLEngine::isIFC2x3())
    {
        // todo implement for ifc2x3
    }

    return false;
}

static Ifc4::IfcValue *handleEnumeratedProperty(boost::optional<boost::shared_ptr<Ifc4::IfcValue::list>> optional);

static Ifc4::IfcValue *findValue(const Ifc4::IfcProperty *property)
{
    try
    {
        return property->as<Ifc4::IfcPropertySingleValue>(true)->NominalValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        const auto enumerated = property->as<Ifc4::IfcPropertyEnumeratedValue>(true);
        return handleEnumeratedProperty(enumerated->EnumerationValues());
    }
    catch (const IfcParse::IfcException &)
    { // ignore me 2
    }

    return nullptr;
}

static Ifc4::IfcValue *handleEnumeratedProperty(const boost::optional<boost::shared_ptr<Ifc4::IfcValue::list>> optional)
{
    if (!optional.has_value())
        return nullptr;

    const auto list = optional.get();
    if (!list->size())
        return nullptr;
    try
    {
        const auto item = (*(list->begin()))->as<Ifc4::IfcLabel>(true);
        return item;
    }
    catch (const IfcParse::IfcException &)
    {
        return nullptr;
    }
}
