package org.egov.id.service;

import java.io.IOException;
import java.util.*;

import org.egov.id.model.IdRequest;
import org.egov.id.model.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.mdms.service.MdmsClientService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class MdmsService {

	@Autowired
	MdmsClientService mdmsClientService;

	// 'tenants' & 'citymodule' are the JSON files inside the folder 'tenant'.
	private static final String tenantMaster = "tenants";
	private static final String cityMaster = "citymodule";
	private static final String tenantModule = "tenant";

	//'IdFormat' is the JSON file in the Folder 'common-masters'.
	private static final String formatMaster = "IdFormat";
	private static final String formatModule = "common-masters";


	public MdmsResponse getMasterData(RequestInfo requestInfo, String tenantId,
			Map<String, List<MasterDetail>> masterDetails) {

		MdmsResponse mdmsResponse = null;
		try {
			mdmsResponse = mdmsClientService.getMaster(RequestInfo.toCommonRequestInfo(requestInfo), tenantId,
					masterDetails);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mdmsResponse;
	}

	/*
	public String getCity(RequestInfo requestInfo, String tenantId, IdRequest idRequest) {

		String idname = idRequest.getIdName();  //


		Map<String, List<MasterDetail>> masterDetails = new HashMap<String, List<MasterDetail>>();
		// masterDetailList is a new list with obj of type MasterDetail
		List<MasterDetail> masterDetailList = new LinkedList();
		//masterDetailForCity
		MasterDetail masterDetailForCity = MasterDetail.builder().name(cityMaster)
				.filter("[?(@.code==" + "'" + tenantId + "'" + ")]").build();
		//adds masterDetailForCity to masterDetailList
		masterDetailList.add(masterDetailForCity);
		//masterDetailForTenant
		MasterDetail masterDetailForTenant = MasterDetail.builder().name(tenantMaster)
				.filter("[?(@.idname==" + "'" +idname+ "'" + ")]").build();

		//adds masterDetailForTenant to masterDetailList
		masterDetailList.add(masterDetailForTenant);
		// mapped as key=tenantModule and value=masterDetailList
		masterDetails.put(tenantModule, masterDetailList);

		MdmsResponse mdmsResponse = null;
		String cityCode = null;
		String IdFormat = null;

		try {

			mdmsResponse = getMasterData(requestInfo, tenantId, masterDetails);

			if (mdmsResponse.getMdmsRes() != null && mdmsResponse.getMdmsRes().containsKey(tenantModule)
					&& mdmsResponse.getMdmsRes().get(tenantModule).containsKey(tenantMaster)) {
				DocumentContext documentContext = JsonPath
						.parse(mdmsResponse.getMdmsRes().get(tenantModule));
				cityCode = documentContext.read("$.tenants[0].city.code");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cityCode;
	}
	*/

	public String getCity(RequestInfo requestInfo, String tenantId,IdRequest idRequest,MdmsCriteria mdmsCriteria) {

	/*	Map<String, List<MasterDetail>> masterDetails = new HashMap<String, List<MasterDetail>>();

		// filter to get data of the specified tenantId
		MasterDetail masterDetail = MasterDetail.builder().name(tenantMaster)
				.filter("[?(@.code==" + "'" + tenantId + "'" + ")]").build();
		masterDetails.put(tenantModule, Arrays.asList(masterDetail));
	*/

		Map<String, String> getCity = doMdmsServiceCall(requestInfo,idRequest, mdmsCriteria,tenantId);
		String cityCode = getCity.get(tenantMaster);
		/*
		try {

			mdmsResponse = getMasterData(requestInfo, tenantId, masterDetails);

			if (mdmsResponse.getMdmsRes() != null && mdmsResponse.getMdmsRes().containsKey(tenantModule)
					&& mdmsResponse.getMdmsRes().get(tenantModule).containsKey(tenantMaster)) {
				DocumentContext documentContext = JsonPath
						.parse(mdmsResponse.getMdmsRes().get(tenantModule));
				cityCode = documentContext.read("$.tenants[0].city.code");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		return cityCode;
	}

	public String getIdFormat(RequestInfo requestInfo, String tenantId, IdRequest idRequest, MdmsCriteria mdmsCriteria) {

	/*	String idname = idRequest.getIdName();
		Map<String, List<MasterDetail>> masterDetails = new HashMap<String, List<MasterDetail>>();
		MasterDetail masterDetail = MasterDetail.builder().name(formatMaster)
				.filter("[?(@.idname==" + "'" +idname+ "'" + ")]").build();
		// 	.filter("[?(@.idname==" + "'" +idname+ "'" + ")]")
		//"[?(@.idname =~ /.*collection.transactionno/i)]"
		masterDetails.put(formatModule, Arrays.asList(masterDetail));
	*/
		Map<String, String> getIdFormat = doMdmsServiceCall(requestInfo,idRequest, mdmsCriteria,tenantId);
		String IdFormat = getIdFormat.get(formatMaster);



	/*	try {

			mdmsResponse = getMasterData(requestInfo, tenantId, masterDetails);

			if (mdmsResponse.getMdmsRes() != null && mdmsResponse.getMdmsRes().containsKey(formatModule)
					&& mdmsResponse.getMdmsRes().get(formatModule).containsKey(formatMaster)) {
				DocumentContext documentContext = JsonPath
						.parse(mdmsResponse.getMdmsRes().get(formatModule));
				IdFormat = documentContext.read("$.IdFormat[0].format");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		return IdFormat;
	}



	/**
	 * Prepares and returns Mdms search request with financial master criteria
	 * 
	 * @param requestInfo
	 * @return
	 */
	private Map<String,String> doMdmsServiceCall(RequestInfo requestInfo,IdRequest idRequest,MdmsCriteria mdmsCriteria, String tenantId) {

		/*
		 * MasterDetail mstrDetail = MasterDetail.builder().name("tenants")
		 * .filter("[?(@.code=="+"'"+tenantId+"'"+")]") .build(); ModuleDetail
		 * moduleDetail = ModuleDetail.builder().moduleName("tenant")
		 * .masterDetails(Arrays.asList(mstrDetail)).build(); MdmsCriteria
		 * mdmsCriteria =
		 * MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).
		 * tenantId(tenantId) .build();
		 */
		String idname = idRequest.getIdName();
		String IdFormat_1= null;
		String citycode_1=null;


		Map<String, List<MasterDetail>> masterDetails = new HashMap<String, List<MasterDetail>>();
		// masterDetailList is a new list with obj of type MasterDetail
		List<MasterDetail> masterDetailListCity = new LinkedList();
		List<MasterDetail> masterDetailListFormat = new LinkedList();
		//masterDetailForCity
		MasterDetail masterDetailForCity = MasterDetail.builder().name(cityMaster)
		.filter("[?(@.code==" + "'" + tenantId + "'" + ")]").build();
		//adds masterDetailForCity to masterDetailList
		masterDetailListCity.add(masterDetailForCity);
		//masterDetailForTenant
		MasterDetail masterDetailForFormat = MasterDetail.builder().name(formatMaster)
				.filter("[?(@.idname==" + "'" +idname+ "'" + ")]").build();

		//adds masterDetailForTenant to masterDetailList
		masterDetailListFormat.add(masterDetailForFormat);
		// mapped as key=tenantModule and value=masterDetailList
		masterDetails.put(tenantModule, masterDetailListCity);
		masterDetails.put(formatModule, masterDetailListFormat);

		MdmsCriteriaReq mdmsCriteriaReq = null;
		MdmsResponse mdmsResponse = null;
		try {

		//	mdmsResponse = getMasterData(requestInfo, tenantId, masterDetails);

			mdmsCriteriaReq = MdmsCriteriaReq.builder().requestInfo(RequestInfo.toCommonRequestInfo(requestInfo))
					.mdmsCriteria(mdmsCriteria).build();
		//	mdmsResponse = mdmsClientService.getMaster(mdmsCriteriaReq);
			mdmsResponse = getMasterData(requestInfo, tenantId, masterDetails);

			if (mdmsResponse.getMdmsRes() != null && mdmsResponse.getMdmsRes().containsKey(tenantModule)
					&& mdmsResponse.getMdmsRes().get(tenantModule).containsKey(tenantMaster) && mdmsResponse.getMdmsRes().get(tenantModule).get(tenantMaster).get(0)!=null) {
				DocumentContext documentContext = JsonPath
						.parse(mdmsResponse.getMdmsRes().get(tenantModule).get(tenantMaster).get(0));

			//	System.out.println(documentContext.jsonString());
				 citycode_1 = documentContext.read("$.code");
				System.out.println(citycode_1);
			}
			if (mdmsResponse.getMdmsRes() != null && mdmsResponse.getMdmsRes().containsKey(formatModule)
					&& mdmsResponse.getMdmsRes().get(formatModule).containsKey(formatMaster) && mdmsResponse.getMdmsRes().get(formatModule).get(formatMaster).get(0)!=null) {
				DocumentContext documentContext = JsonPath
						.parse(mdmsResponse.getMdmsRes().get(formatModule).get(formatMaster).get(0));
		//		System.out.println(documentContext.jsonString());
				IdFormat_1 = documentContext.read("$.format");
		//		System.out.println(IdFormat_1);
			}

		}
			catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CustomException("PARSING ERROR","Failed to get citycode/formatid from MDMS");
		}
		Map<String, String> out= new HashMap();
		out.put(formatMaster, IdFormat_1);
		out.put(tenantMaster, citycode_1);

		return out;
	}

}
