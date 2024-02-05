#include <ifcparse/IfcBaseClass.h>
#include <ifcparse/Ifc4.h>
#include <ifcparse/Ifc2x3.h>

#include <boost/optional.hpp>

#include "utils/ifc_quantities.hpp"
#include "utils/OpenBIMRL/ifcStandards.hpp"

// TODO: rework. See ifc_properties for reference

static std::vector<Ifc4::IfcElementQuantity *> quantities;

std::size_t initQuantityIterator(JNA::Pointer ifcPointer)
{
    quantities.clear();

    const auto currentPointer = static_cast<IfcUtil::IfcBaseClass *>(ifcPointer);

    if (!OpenBIMRLEngine::isIFC4())
        return 0;

    const auto instanceData = currentPointer->as<Ifc4::IfcObject>();
    const auto definitions = instanceData->IsDefinedBy();
    for (auto definition : *definitions)
    {
        try
        {
            auto quantitySet = definition->RelatingPropertyDefinition()->as<Ifc4::IfcElementQuantity>(true);
            quantities.push_back(quantitySet);
        }
        catch (const IfcParse::IfcException &)
        {
            continue; // ignore this element
        }
    }

    // after filling the list
    return quantities.size();
}

std::size_t getBufferSizeQuantitySetName(std::size_t index)
{
    // failsafe index check
    if (index >= quantities.size())
        return 0;

    const auto name = get_optional_value_or(quantities.at(index)->Name(), "");
    return name.length();

    return 0;
}

std::size_t getBufferSizeQuantityName(std::size_t quantitySetIndex, std::size_t quantityIndex)
{
    // failsafe index check
    if (quantitySetIndex >= quantities.size())
        return 0;

    const auto quants = quantities.at(quantitySetIndex)->Quantities();

    if (quantityIndex >= quants->size())
        return 0;

    const auto quant = *(quants->begin() + quantityIndex);
    return quant->Name().length();

    return 0;
}

std::size_t getNoOfQuantitiesInSet(std::size_t index)
{
    // failsafe index check
    if (index >= quantities.size())
        return 0;

    return quantities.at(index)->Quantities()->size();
}

bool getQuantitySetName(std::size_t index, JNA::String buffer)
{
    if (index >= quantities.size())
        return false;

    const auto name = get_optional_value_or(quantities.at(index)->Name(), "");
    memcpy(buffer, name.c_str(), name.size());
    return true;
}

bool getQuantityName(std::size_t quantitySetIndex, std::size_t quantityIndex, JNA::String buffer)
{
    // failsafe index check
    if (quantitySetIndex >= quantities.size())
        return false;

    const auto props = quantities.at(quantitySetIndex)->Quantities();
    if (quantityIndex >= props->size())
        return false;
    const auto propName = (*(props->begin() + quantityIndex))->Name();

    memcpy(buffer, propName.c_str(), propName.size());

    return true;
}

double getQuantityValue(std::size_t quantitySetIndex, std::size_t quantityIndex)
{
    auto failureValue = -1.0;

    // failsafe index check
    if (quantitySetIndex >= quantities.size())
        return failureValue;

    const auto quants = quantities.at(quantitySetIndex)->Quantities();
    if (quantityIndex >= quants->size())
        return failureValue;

    Ifc4::IfcPhysicalSimpleQuantity *quantity = nullptr;

    try
    {
        quantity = (*(quants->begin() + quantityIndex))->as<Ifc4::IfcPhysicalSimpleQuantity>(true);
    }
    catch (const IfcParse::IfcException &)
    {
        // maybe handle complex properties? NAH!
        return failureValue;
    }

    // this is horrible 2
    try
    {
        return quantity->as<Ifc4::IfcQuantityArea>(true)->AreaValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        return quantity->as<Ifc4::IfcQuantityCount>(true)->CountValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        return quantity->as<Ifc4::IfcQuantityLength>(true)->LengthValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        return quantity->as<Ifc4::IfcQuantityTime >(true)->TimeValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        return quantity->as<Ifc4::IfcQuantityVolume >(true)->VolumeValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me
    }
    try
    {
        return quantity->as<Ifc4::IfcQuantityWeight>(true)->WeightValue();
    }
    catch (const IfcParse::IfcException &)
    { // ignore me2
    }

    return failureValue;
}